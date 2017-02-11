(ns com.agilecreativity.hn_collector.main
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.tools.cli :refer [parse-opts] :as cli]
            [com.agilecreativity.hn_collector.option :refer :all :as opt]
            [me.raynes.fs :as fs]
            [reaver :refer [parse extract-from extract text attr]])
  (:gen-class))

(declare extract-data
         org-links
         hn-link-url
         hn-link-url-item
         hacker-news-url
         create-url
         discussion-url
         hacker-news
         repeat-string
         hn-make-org-list)

;; Common constants to make it easy to change
(def hn-title-level 3)
(def hn-page-level 4)
(def hn-url-level 5)
(def hn-comment-level 0) ;; Note: make comment just regular level
(def hn-sleep-between-new-request 200) ;; in mili-second
(def comment-label "Comments")

(defn- hacker-news
  "Read the content of hacker-news for a given page"
  [page-number]
  (slurp (hacker-news-url page-number)))

(defn- discussion-url
  [vote-url]
  (let [article-id (re-find #"\d+" vote-url)
        article-url (str "https://news.ycombinator.com/item?id=" article-id)]
    ;; TODO: just add this to the end of the list
    (hn-link-url-item article-url comment-label hn-comment-level)))

(defn- create-url
  "Create simple link for a given URL."
  [item]
  (if-let [vote-url (:vote-url item)]
    ;; Only link to the original article if we can extract the link properly
    (let [head-line (:headline item)
          main-url (:url item)
          comment-url (discussion-url vote-url)]
      (str (hn-link-url-item main-url head-line hn-url-level) "::" comment-url))

    ;; TODO: need to find other way to get the article id (no comment link for now)!
    (hn-link-url-item (:url item) (:headline item) hn-url-level)))

(defn- hacker-news-url
  "Return URL from hacker-news for a given page"
  [page-number]
  (str "https://news.ycombinator.com/news?p=" page-number))

;; Public APIs
(defn org-links
  "Create basic markdown link from content map"
  [content]
  (map create-url content))

(defn extract-data
  "Extract the data from the web page for a given page-number"
  [page-number]
  (let [content (parse (hacker-news page-number))]
    (do
      (extract-from content ".itemlist .athing"
                    [:vote-url :headline :url]
                    ".voteLinks a" (attr :href)
                    ".title > a" text
                    ".title > a" (attr :href)))))

(defn- sanitized-square-brackets
  "Replace square brackets with normal brackets to make valid org-mode link."
  [url-desc]
  (->
   url-desc
   (clojure.string/replace "[" "(")
   (clojure.string/replace "]" ")")))

(defn hn-link-url
  "convert a given url to org-mode link text"
  ([url]
   (str "[[" url "][" url "]]"))
  ([url url-desc]
   (str "[[" url "][" url-desc "]]")))

(defn hn-link-url-item
  ([url url-desc]
   (hn-link-url-item url url-desc 0))
  ([url url-desc level]
   (str
    (if (pos? level)
      ;; Create org list if level is positive
      (hn-make-org-list level "-")
      "")
    (hn-link-url url (sanitized-square-brackets url-desc)))))

(defn hn-make-org-list
  "Make org list to a given level of indentation."
  ([level]
   (hn-make-org-list level "-"))
  ([level list-char]
   (str (repeat-string level " ")
        list-char
        " ")))

(defn repeat-string
  "Repeat a given string string in a given time."
  [n string]
  (str (clojure.string/join (repeat n string))))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]}
        (cli/parse-opts args opt/options)]
    (cond
      (:help options)
      (exit 0 (usage summary)))
    ;; If we get here, then we are ready to go
    (let [output-file (-> (:output-file options)
                          (fs/expand-home)
                          (fs/normalized))
          page-count (:page-count options)]
      (do
        (with-open [w (io/writer output-file)]
          (.write w (str (repeat-string hn-title-level "*")
                         " "
                         "The recent " page-count " pages from Hacker News\n"))
          (.newLine w)
          ;; Note: Hacker News only show the last 20 pages
          (doseq [n (range (Integer. page-count))]
            (let [content (extract-data (inc n))]
              (comment (clojure.pprint/pprint content))
              ;; Sleep between each request to be polite
              (Thread/sleep hn-sleep-between-new-request)
              (.write w (str (repeat-string hn-page-level "*")
                             " "
                             "page " (+ 1 n) " of " page-count "\n"))

              (doseq [line (org-links content)]
                ;; Note: can be suppress if required!
                (println line)
                (.write w line)
                (.newLine w)))))))))
