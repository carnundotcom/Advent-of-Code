(ns utils
  (:require [babashka.fs :as fs]
            [clojure.string :as str]))

(defn input-filename []
  (as-> (str *ns*) s
    (str/split s #"\.")
    (interleave (repeat "/") s)
    (apply str s)
    (str "data" s ".txt")))

(defn input []
  (fs/read-all-lines (input-filename)))
