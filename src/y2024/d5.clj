(ns y2024.d5
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input ["47|53"
                  "97|13"
                  "97|61"
                  "97|47"
                  "75|29"
                  "61|13"
                  "75|53"
                  "29|13"
                  "97|29"
                  "53|29"
                  "61|53"
                  "97|53"
                  "61|29"
                  "47|13"
                  "75|47"
                  "97|75"
                  "47|61"
                  "75|61"
                  "47|29"
                  "75|13"
                  "53|13"
                  ""
                  "75,47,61,53,29"
                  "97,61,53,29,13"
                  "75,29,13"
                  "75,97,47,61,53"
                  "61,13,29"
                  "97,13,75,29,47"])
(def input (u/input))

; ----------------------------------------------------------------------------------------------------------------------
; Parsing

(defn parse
  "Returns a tuple of [<rules set>, <updates vector>]."
  [input]
  (let [[rules _ updates]             (partition-by empty? input)]
    [(set (mapv #(str/split % #"\|") rules)) ; e.g. #{["47" "53"], ["97" "13"], ...}
     (mapv #(str/split % #",") updates)]))   ; e.g. [["75", "47", "61", "53", "29"] ...]

(defn update->rules
  "Returns a (lazy) sequence of [<string> <string>] 'rules' — the rules actually governing update-v!"
  [update-v]
  (for [i (range 0 (dec (count update-v)))
        j (range (inc i) (count update-v))]
    [(get update-v i) (get update-v j)]))

; ----------------------------------------------------------------------------------------------------------------------
; Solving

(defn disordered?
  [rules-set update-rules]
  ; An update-v is 'disordered' if the rules-set contains one or more 'anti rules' of its corresponding
  ; (update->rules update-v). Hence:
  (let [anti-rule (fn [[a b]] [b a])]
    (some #(contains? rules-set (anti-rule %)) update-rules)))

(defn middle-n
  [update-v]
  (parse-long (get update-v (int (/ (count update-v) 2)))))

(defn part-1 [rules-set updates update-rules]
  (->> updates
       ; Keep only the 'middle page number' of the *ordered* update-v
       (keep-indexed (fn [i update-v]
                       (when-not (disordered? rules-set (get update-rules i))
                         (middle-n update-v))))
       ; And sum
       (apply +)))

(defn ->compare
  "Returns a comparator for the rules-set."
  [rules-set]
  (fn [a b]
    (cond
      (contains? rules-set [a b]) -1
      (contains? rules-set [b a]) 1
      :else                       0)))

(defn part-2 [rules-set updates update-rules]
  (->> updates
       ; Keep only the *disordered* update-v
       (keep-indexed (fn [i update-v]
                       (when (disordered? rules-set (get update-rules i))
                         update-v)))
       ; Sort them
       (map #(middle-n (vec (sort-by identity (->compare rules-set) %))))
       ; And sum
       (apply +)))

(defn solve
  [input]
  (let [[rules-set updates] (parse input)
        updates-rules       (mapv update->rules updates)]
    ; Time just the solving, not also the parsing
    {:part-1 (time (part-1 rules-set updates updates-rules))
     :part-2 (time (part-2 rules-set updates updates-rules))}))

; ----------------------------------------------------------------------------------------------------------------------

(tests
  (solve dummy-input) := {:part-1 143, :part-2 123}
  )

(comment
  (solve input)
  ; => "Elapsed time: 4.688307 msecs"
  ;    "Elapsed time: 4.243465 msecs"
  ;    {:part-1 4135, :part-2 5285}

  ; => "Elapsed time: 6.171097 msecs"
  ;    "Elapsed time: 5.606293 msecs"
  ;    {:part-1 4135, :part-2 5285}

  ; => "Elapsed time: 2.988071 msecs"
  ;    "Elapsed time: 4.611818 msecs"
  ;    {:part-1 4135, :part-2 5285}
  )
