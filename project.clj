(defproject hn-reader "0.2.3"
  :description "Easy ways to access all of the latest Hacker News links in one page"
  :url "https://github.com/agilecreativity/hn-reader"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[lein-binplus "0.6.5"]
                                  [com.jakemccrary/lein-test-refresh "0.23.0"]]}
             :uberjar {:aot :all}}
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :bin {:name "hn-reader"
        :bin-path "~/bin"
        :bootclasspath true}
  :plugins [[lein-binplus "0.6.5"]
            [lein-cljfmt "0.6.4"]
            [lein-auto "0.1.3"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [reaver "0.1.2"]
                 [org.clojure/tools.cli "0.4.1"]
                 [akvo/fs "20180904-152732.6dad3934"]]
  :main com.agilecreativity.hn_reader.main)
