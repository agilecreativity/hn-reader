(ns com.agilecreativity.hn_collector.main
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.tools.cli :refer [parse-opts] :as cli]
            [com.agilecreativity.hn_collector.option :refer :all :as opt]
            [me.raynes.fs :as fs]
            [markdown.core :as md]
            [reaver :refer [parse extract-from extract text attr]])
  (:gen-class))

(defn- hacker-news-url
  "Return URL from hacker-news for a given page"
  [page-number]
  (str "https://news.ycombinator.com/news?p=" page-number))

(defn- hacker-news
  "Read the content of hacker-news for a given page"
  [page-number]
  (slurp (hacker-news-url page-number)))

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

(defn- discussion-url
  [vote-url]
  (let [article-id (re-find #"\d+" vote-url)]
    (str "[Comments](https://news.ycombinator.com/item?id=" article-id ")")))

(defn- create-url
  "Create simple link for a given URL."
  [item]
  (if-let [vote-url (:vote-url item)]
    ;; Only link to the original article if we can extract the link properly
    (str "[" (:headline item) "](" (:url item) ") :: " (discussion-url vote-url))
    ;; TODO: need to find other way to get the article id
    (str "[" (:headline item) "](" (:url item) ")")))

(defn markdown-links
  "Create basic markdown link from content map"
  [content]
  (map create-url content))

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
        ;; (md/md-to-html "## Hello")
        ;; Show the output file path if required
        ;; (println "Output file : " output-file)
        (with-open [w (io/writer output-file)]
          (.write w (str "### The last " page-count " pages from Hacker News"))
          (.newLine w)
          ;; Note: Hacker News only show the last 20 pages
          (doseq [n (range (Integer. page-count))]
            (let [content (extract-data (inc n))]
              ;; Let sleep 500 mili-second between new request to be polite
              (Thread/sleep 500)
              (doseq [line (markdown-links content)]
                ;; Show something to get a better experience
                (println line)
                ;; Create a list in Markdown format
                (.write w (str " - " line))
                (.newLine w)))))))))
