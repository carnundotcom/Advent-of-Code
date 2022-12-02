(ns y2022.d2
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["A Y" "B X" "C Z"])
(def input (u/input))

; ---

(defn played+outcomes [input & {:keys [part-2?]}]
  (let [played (fn [round-str]
                 (if-not part-2?
                   (get {\X :rock \Y :paper \Z :scissors} (last round-str))
                   (get {"A X" :scissors "A Y" :rock "A Z" :paper
                         "B X" :rock "B Y" :paper "B Z" :scissors
                         "C X" :paper "C Y" :scissors "C Z" :rock}
                        round-str)))
        outcome (fn [round-str]
                  (if-not part-2?
                    (get {"A X" :draw "A Y" :win "A Z" :loss
                          "B X" :loss "B Y" :draw "B Z" :win
                          "C X" :win "C Y" :loss "C Z" :draw}
                         round-str)
                    (get {\X :loss \Y :draw \Z :win} (last round-str))))]
    (map #(vector (played %) (outcome %)) input)))

(defn score [[played outcomes]]
  (let [played-score {:rock 1 :paper 2 :scissors 3}
        outcome-score {:loss 0 :draw 3 :win 6}]
    (+ (played-score played) (outcome-score outcomes))))

(defn total-score [input part]
  (->> (played+outcomes input :part-2? (= :part-2 part))
       (map score)
       (reduce +)))

(tests
  (total-score dummy-input :part-1) := 15
  (total-score dummy-input :part-2) := 12)

(comment
  (total-score input :part-1) ; => 15632
  (total-score input :part-2) ; => 14416
  )
