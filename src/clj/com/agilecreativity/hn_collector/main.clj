(ns com.agilecreativity.hn_collector.main
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.tools.cli :refer [parse-opts] :as cli]
            [com.agilecreativity.hn_collector.option :refer :all :as opt]
            [me.raynes.fs :as fs]
          ;;[markdown.core :as md]
            [reaver :refer [parse extract-from extract text attr]])
  ;; TODO: when it is appropriate to use :gen-class?
  (:gen-class))

(declare extract-data
         markdown-links
         hn-link-url
         hn-link-url-item
         hacker-news-url
         create-url
         discussion-url
         hacker-news)

(defn- hacker-news
  "Read the content of hacker-news for a given page"
  [page-number]
  (slurp (hacker-news-url page-number)))

(defn- discussion-url
  [vote-url]
  (let [article-id (re-find #"\d+" vote-url)]
    ;; TODO: move https://news.ycombinator.com to a def for code reuse
    (str "[Comments](https://news.ycombinator.com/item?id=" article-id ")")))

(defn- discussion-url-new
  [vote-url]
  (let [article-id (re-find #"\d+" vote-url)
        article-url (str "https://news.ycombinator.com/item?id=" article-id)]
    ;; Now we can use this as input to org-mode link
    ;; Note: create 2nd level heading for now
    (hn-link-url-item article-url "Comments" 4)))

(defn- create-url
  "Create simple link for a given URL."
  [item]
  (if-let [vote-url (:vote-url item)]
    ;; Only link to the original article if we can extract the link properly
    (let [head-line (:headline item)
          main-url (:url item)
          comment-url (discussion-url-new vote-url)]
      ;; NOTE:
      (str
       (hn-link-url-item main-url head-line 3)
       "\n"
       comment-url))

    ;; TODO: need to find other way to get the article id (no comment link for now)!
    ;;(comment (str "[" (:headline item) "](" (:url item) ")"))
    (hn-link-url-item (:url item) (:headline item) 3)))

(defn- hacker-news-url
  "Return URL from hacker-news for a given page"
  [page-number]
  (str "https://news.ycombinator.com/news?p=" page-number))

;; Public APIs
(defn markdown-links
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

;; New stuffs starts here

(defn- sanitized-square-brackets
  "Replace square brackets with normal brackets for org-mode link."
  [url-desc]
  (->
   url-desc
   (clojure.string/replace "[" "(")
   (clojure.string/replace "]" ")")))

(defn hn-link-url
  "convert a given url to org-mode link text"
  ([url]
   (str "[[" url "][" url "]]"))
  ([url link-desc]
   (str "[[" url "][" link-desc "]]")))

(defn hn-link-url-item
  "convert a given url to org-mode link text with bullet point."
  ([url]
   (hn-link-url-item url url 1))
  ([url url-desc]
   (hn-link-url-item url url-desc 1))
  ([url url-desc depth]
   (str (clojure.string/join (repeat depth "*"))
        " "
        (hn-link-url url (sanitized-square-brackets url-desc)))))

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
        ;; Show the output file path if required
        ;; (println "Output file : " output-file)
        (with-open [w (io/writer output-file)]
          (.write w (str "** The last " page-count " pages from Hacker News\n"))
          (.newLine w)
          ;; Note: Hacker News only show the last 20 pages
          (doseq [n (range (Integer. page-count))]
            (let [content (extract-data (inc n))]
              ;; NOTE: for debugging only
              (comment (clojure.pprint/pprint content))
              ;; Let sleep 500 mili-second between new request to be polite
              (Thread/sleep 500)
              (.write w (str "** page " (+ 1 n) " of " page-count "\n"))
              (doseq [line (markdown-links content)]
                ;; Show something to get a better experience
                ;;(println line)
                ;; Create a list in Markdown format
                (.write w line)
                (.newLine w)))))))))