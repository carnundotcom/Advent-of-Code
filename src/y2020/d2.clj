(ns y2020.d2
  (:require
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input ["1-3 a: abcde"
                  "1-3 b: cdefg"
                  "2-9 c: ccccccccc"])
(def input (u/input))

; ---

(defn parse [line]
  (let [[_ a b ch password] (re-find #"(\d+)-(\d+) ([a-z]): ([a-z]+)" line)]
    [(parse-long a), (parse-long b), (first ch), password]))

(defn part-1 [input]
  (count
    (filter #(let [[gt lt ch password] (parse %)
                   freqs               (frequencies password)]
               (<= gt (get freqs ch 0) lt))
            input)))

(defn part-2 [input]
  (count
    (filter #(let [[p1 p2 ch password] (parse %)]
               (= 1 (+ (if (= ch (get password (dec p1))) 1 0)
                       (if (= ch (get password (dec p2))) 1 0))))
            input)))

(tests
  (part-1 dummy-input) := 2
  (part-2 dummy-input) := 1
  )

(comment
  (part-1 input) ; => 456
  (part-2 input) ; => 308
  )
