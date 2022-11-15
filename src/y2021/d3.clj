(ns y2021.d3
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["00100" "11110" "10110" "10111" "10101" "01111" "00111" "11100" "10000" "11001" "00010" "01010"])
(def input (u/input))

; ---

(defn slice-freqs [input bit]
  (frequencies (map #(subs % bit (inc bit)) input)))

(defn rates
  ([freqs] (rates freqs :o2))
  ([freqs rating-type]
   (let [[[k1 v1] [k2 v2]] (seq freqs)]
     (cond
       (< v1 v2) {:g k2 :e k1}
       (< v2 v1) {:g k1 :e k2}
       ;; part 2 tiebreakers
       (= :o2 rating-type) {:g "1" :e "0"}
       (= :co2 rating-type) {:g "0" :e "1"}))))

(defn part-1 [input]
  (let [bits (count (first input))]
    (->> (for [bit (range bits)]
           (slice-freqs input bit))
         (reduce #(merge-with str %1 (rates %2)) {})
         vals
         (map #(Long/parseLong % 2))
         (reduce *))))

(tests
  (slice-freqs dummy-input 0) := {"0" 5, "1" 7}
  (part-1 dummy-input) := 198)

(comment
  (part-1 input) ; => 2743844
  )

; ---

(defn rating [input rating-type]
  (loop [values input
         bit 0]
    (if (< (count values) 2)
      (first values)
      (let [{:keys [g e]} (rates (slice-freqs values bit)) ]
        (recur
          (filter #(= (condp = rating-type, :o2 g :co2 e)
                      (subs % bit (inc bit))) values)
          (inc bit))))))

(defn part-2 [input]
  (let [o2 (Long/parseLong (rating input :o2) 2)
        co2 (Long/parseLong (rating input :co2) 2)]
    (* o2 co2)))

(tests
  (part-2 dummy-input) := 230)

(comment
  (part-2 input) ; => 6677951
  )
