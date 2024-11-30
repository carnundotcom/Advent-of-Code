(ns y2020.d1
  (:require
    [clojure.math.combinatorics :as combo]
    [utils :as u]))

(def dummy-input ["1721" "979" "366" "299" "675" "1456"])
(def input (u/input))

; ---

(def goal-sum 2020)

; Part One solution, before modification:
#_(defn solve [input]
    (->> ; generate lazy seq of [i j] pairs, excluding when i = j
         ; (because we want 'two entries that sum to 2020...')
         (for [i (range (count input))
               j (range (count input))
               :when (not= i j)]
           [i j])
         ; test each pair, stopping when the goal pair is found
         (reduce (fn [_ans [i j]]
                   (let [a (parse-long (get input i))
                         b (parse-long (get input j))
                         sum (+ a b)]
                     (when (= sum goal-sum)
                       (reduced (* a b))))))))

; Solution modified to support Part Two:
(defn solve [input n]
  (->> (combo/combinations input n)
       (reduce (fn [_ans tuple]
                 (let [num-tuple (map parse-long tuple)
                       sum       (apply + num-tuple)]
                   (when (= sum goal-sum)
                     (reduced (apply * num-tuple))))))))
(comment
  (solve dummy-input 2)
  ; => 514579

  (solve input 2) ; => 646779
  (solve input 3) ; => 246191688
  )
