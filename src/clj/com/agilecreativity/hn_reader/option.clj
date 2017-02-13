(ns com.agilecreativity.hn_reader.option
  (:require [clojure.string :as string])
  (:gen-class))

(def options
  [["-p" "--page-count PAGE-COUNT" :default "20"]
   ["-o" "--output-file OUTPUT-FILE" :default "hacker-news.org"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Extract the lastest Hacker News index to a single file"
        ""
        "Usage: hn_reader [options]"
        options-summary
        "Options:"
        ""
        "--p PAGE-COUNT  the number of pages to be extracted default to 20"
        "--o OUTPUT-FILE the output file name default to 'hacker-news.org'"
        ""]
       (string/join \newline)))

(defn error-message [errors]
  (str "The following error occured while parsing your commands: \n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))
