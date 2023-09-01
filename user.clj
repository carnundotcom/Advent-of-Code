(ns user
  (:require [babashka.fs :as fs]
            [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [hyperfiddle.rcf :as rcf]
            ; ---
            [utils :as u]))

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
   (setup (.getYear (java.time.LocalDate/now)) day))
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
         session-token (:session-token (edn/read-string (slurp "env.edn")))
         {:keys [status body]} (client/get (format "https://adventofcode.com/%s/day/%s/input" year day)
                                           {:headers {"Cookie" (str "session=" session-token)}})]
     (if (= 200 status)
       (do (println "downloaded puzzle input")
           (write-file dirname filename body)
           (println (format " -> created data/y%s/d%s.txt" year day)))
       "puzzle input download failed"))
   ;; new line in readme
   (let [lines (-> (slurp "README.md")
                   (str/split #"\n"))
         table-start-index (dec (u/some-i #(re-find #"^\| --- \| --- \| --- \|" %) lines))
         year->table-lines (-> (group-by #((fnil parse-long "") (second (re-find #"^\| (\d{4}) \|" %))) lines)
                               (dissoc nil))
         year->new-table-lines (update year->table-lines
                                       year #(->> (conj % (format "| %s | [%s](src/y%s/d%s.clj) | |" year day year day))
                                                  (sort-by (fn [s] (parse-long (second (re-find #"\[(\d{1,2})\]" s)))))))
         new-table-lines (->> (sort (keys year->new-table-lines))
                              (map year->new-table-lines)
                              (interleave (repeat '("| | | |")))
                              (drop 1)
                              (apply concat))]
     (spit "README.md" (->> (concat (take table-start-index lines)
                                    '("| year | day | summary |" "| --- | --- | --- |")
                                    new-table-lines
                                    '("" "Feedback welcome!"))
                            (map #(str % "\n"))
                            (apply str)))
     (println "added a blank line to README.md"))))
