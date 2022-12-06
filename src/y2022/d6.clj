(ns y2022.d6
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-inputs ["mjqjpqmgbljsphdztnvjfqwrcgsmlb"
                   "bvwbjplbgvbhsrlpgdmjqwftvncz"
                   "nppdvjthqldpwncqszvftbrmjlhg"
                   "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"
                   "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"])
(def input (first (u/input)))

; ---

(defn find-marker [n input]
  (->> (partition n 1 input)
       (keep-indexed #(when (apply distinct? %2) %1))
       first
       (+ n)))

(tests
  (mapv (partial find-marker 4) dummy-inputs) := [7 5 6 10 11]
  (mapv (partial find-marker 14) dummy-inputs) := [19 23 23 29 26])

(comment
  (find-marker 4 input) ; => 1042
  (find-marker 14 input) ; => 2980
  )
