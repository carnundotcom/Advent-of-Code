(ns user
  (:require [babashka.fs :as fs]
            [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [hyperfiddle.rcf :as rcf]))

(rcf/enable!)

; ---

(defn- write-file [dirname filename contents-str]
  (when-not (fs/exists? filename)
    (when-not (fs/directory? dirname)
      (fs/create-dir dirname))
    (fs/create-file filename)
    (spit filename contents-str)))

(defn setup
  ([]
   (let [date (java.time.LocalDate/now)]
     (setup (.getYear date) (.getDayOfMonth date))))
  ([day]
   (setup (java.time.LocalDate/now) day))
  ([year day]
   ;; create new solution file
   (let [dirname (str "src/y" year "/")
         filename (str dirname "d" day ".clj")
         boilerplate
         (str "(ns y" year ".d" day"\n  (:require [hyperfiddle.rcf :refer [tests]]\n            [utils :as u]))\n"
              "\n"
              "(def dummy-input [\n\n])\n"
              "(def input (u/input))\n"
              "\n"
              "; ---\n"
              "\n\n\n"
              "(tests\n\n)\n"
              "\n"
              "(comment\n\n)")]
     (write-file dirname filename boilerplate)
     (println (format "created src/y%s/d%s.clj" year day)))
   ;; download puzzle input
   (let [dirname (str "data/y" year "/")
         filename (str dirname "d" day ".txt")
         session-token (-> (edn/read-string (slurp "env.edn"))
                           (get-in [:session-tokens year]))
         {:keys [status body]} (client/get (format "https://adventofcode.com/%s/day/%s/input" year day)
                                           {:headers {"Cookie" (str "session=" session-token)}})]
     (if (= 200 status)
       (do (println "downloaded puzzle input")
           (write-file dirname filename body)
           (println (format " -> created data/y%s/d%s.txt" year day)))
       "puzzle input download failed"))
   ;; new line in readme
   (let [readme-lines (-> (slurp "README.md")
                          (str/split #"\n"))
         split-i (->> readme-lines
                      (keep-indexed (fn [i line]
                                      (when-let [table-line (re-find #"src/y\d{4}/d\d{1,2}\.clj" line)]
                                        (let [[y d] (->> (re-find #"src/y(\d{4})/d(\d{1,2})\.clj" table-line)
                                                         rest
                                                         (map parse-long))]
                                          (when (and (<= y year) (<= d day))
                                            i)))))
                      (apply max)
                      inc)
         [before after] (split-at split-i readme-lines)]
     (spit "README.md" (->> (concat before [(format "| %s | [%s](src/y%s/d%s.clj) | |" year day year day)] after)
                            (map #(str % "\n"))
                            (apply str)))
     (println "added a blank line to README.md"))))
