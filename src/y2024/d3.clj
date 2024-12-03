(ns y2024.d3
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

; Why did I treat the example inputs here as vectors of (single) strings? Simply because my u/input parses the actual
; puzzle input into a vector of strings!
;
; One of these days I may start writing new parsing utils, so that I can speed up the inevitable parsing step that
; every one of these problems begins with. But so far... well, a vector of strings has always seemed just fine. :)
(def example-input-1 ["xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))"])
(def example-input-2 ["xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"])
(def input (u/input))

; ----------------------------------------------------------------------------------------------------------------------

(defn parse-line
  [line]
  (let [match->op+nums
        (fn [[_ op n1 n2]]
          (let [op-kw (keyword op)]
            (if (= :mul op-kw)
              [op-kw (parse-long n1) (parse-long n2)]
              [op-kw])))]
    (->> (re-seq #"((?:mul|do|don't))\((?:(\d{1,3}),(\d{1,3}))?\)" line)
         (map match->op+nums))))

; ----------------------------------------------------------------------------------------------------------------------

(defn solve
  [input & {:keys [:part] :or {part 1}}]
  (->> (mapcat parse-line input)
       (reduce (fn [{:keys [do? _total] :as m} [op n1 n2]]
                 (cond
                   (and (or (= 1 part) do?) (= :mul op))
                   (update m :total + (* n1 n2))

                   (and do? (= :don't op))
                   (assoc m :do? false)

                   (and (not do?) (= :do op))
                   (assoc m :do? true)

                   :else m))
               {:do? true
                :total 0})
       (:total)))


(tests
  (solve example-input-1 {:part 1}) := 161
  (solve example-input-2 {:part 2}) := 48
  )

(comment
  (solve input {:part 1})
  (solve input {:part 2})
  )
