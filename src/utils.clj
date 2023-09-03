(ns utils
  (:require [babashka.fs :as fs]
            [clojure.string :as str])
  (:import java.security.MessageDigest
           java.math.BigInteger))


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


;; --- sequences ---

(defn some-i
  "Returns the index of the first item where (f item) is truthy, else nil."
  [f coll]
  (first (keep-indexed #(when (f %2) %1) coll)))


;; --- hashing ---

;; with thanks to https://gist.github.com/jizhang/4325757?permalink_comment_id=2196746#gistcomment-2196746
(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))
