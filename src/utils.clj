(ns utils
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(defn input-filename []
  (->> (str/split (str *ns*) #"\.")
       (apply format "data/%s/%s.txt")))

(defn input []
  (fs/read-all-lines (input-filename)))
