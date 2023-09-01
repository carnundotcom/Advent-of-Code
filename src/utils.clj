(ns utils
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))


;; --- input ---

(defn- input-filename [ns]
  (->> (str/split (str ns) #"\.")
       (apply format "data/%s/%s.txt")))

(defn input
  "Read all lines from puzzle input into a vector of strings.

  By default, reads the puzzle input corresponding to the calling namespace."
  ([] (input *ns*))
  ([ns]
   (fs/read-all-lines (input-filename ns))))
