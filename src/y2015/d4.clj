(ns y2015.d4
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))


(def input (first (u/input)))

; ---

(defn chug-lazy-1
  "Chugs through hashes until one is found that starts with the specified number of zeros."
  [s n]
  (let [found? #(= (subs (u/md5 (str s %)) 0 n)
                   (apply str (repeat n \0)))]
    (some #(when (found? %) %)
          (range 1 ##Inf))))

(defn chug-lazy-2 ; streamline check
  "Chugs through hashes until one is found that starts with the specified number of zeros."
  [s n]
  (let [target (apply str (repeat n \0))]
    (some #(when (str/starts-with? (u/md5 (str s %)) target) %)
          (range 1 ##Inf))))

(defn chug-imperative-1 ; loop instead of traversing a lazy seq
  "Chugs through hashes until one is found that starts with the specified number of zeros."
  [s n]
  (let [target (apply str (repeat n \0))]
    (loop [salt 1]
      (if (str/starts-with? (u/md5 (str s salt)) target)
        salt
        (recur (inc salt))))))

(tests
  (subs (u/md5 "abcdef609043") 0 5) := "00000"
  )

(comment
  (time (chug-lazy-1 input 5))
  ;;="Elapsed time: 496.611692 msecs"
  ;;282749

  (time (chug-lazy-2 input 5))
  ;;="Elapsed time: 399.399964 msecs" ; marginally faster
  ;282749

  (time (chug-lazy-1 input 6))
  ;;="Elapsed time: 17432.438005 msecs"
  ;9962624

  (time (chug-lazy-2 input 6)) ; a few seconds shaved off, nice!
  ;;="Elapsed time: 14137.075444 msecs"
  ;9962624

  (time (chug-imperative-1 input 5)) ; but imperative...
  ;;="Elapsed time: 385.39226 msecs"
  ;282749

  (time (chug-imperative-1 input 6)) ; ... wins here. just!
  ;;="Elapsed time: 13379.78284 msecs"
  ;9962624
  )
