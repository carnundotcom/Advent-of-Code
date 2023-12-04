(ns y2023.d4
  (:require [clojure.math :as math]
            [clojure.set :as set]
            [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input
  ["Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53"
   "Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19"
   "Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1"
   "Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83"
   "Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36"
   "Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11"])
(def input (u/input))

; --- input parsing ---

(defn- parse-card [s]
  (let [[_ winning nums] (re-find #"Card +\d+: +(.*) +\| +(.*)" s)
        f (fn [s] (->> (str/split s #" +") (map parse-long) set))]
    {:winning (f winning)
     :nums (f nums)}))

(defn parse [input]
  (mapv parse-card input))

; --- solving ---

(defn points [{:keys [winning nums] :as _card}]
  (int (math/pow 2.0 (dec (count (set/intersection winning nums))))))

(defn copy-cards
  "Reduce over cards, accumulating copies. Return total copies."
  [cards]
  (->> (map-indexed vector cards) ; generate an index
       (reduce (fn [copies [i {:keys [winning nums]}]]
                 (let [n (count (set/intersection winning nums))]
                   ; make <number of copies of this card> copies of (i+1)th -> (i+n)th cards
                   (merge-with + copies (zipmap (range (inc i) (+ (inc i) n))
                                                (repeat (get copies i))))))
               ; init copies to {0 1, 1 1, 2 1, ...} map
               ; one entry for each card
               (zipmap (range (count cards)) (repeat 1)))
       (vals)
       (apply +)))

(defn solve [input & {:keys [part]}]
  (cond->> (parse input)
    (= 1 part) (map points)
    (= 1 part) (apply +)
    (= 2 part) (copy-cards)))

(tests
  (solve dummy-input :part 1) := 13
  (solve dummy-input :part 2) := 30
  )

(comment
  (solve input :part 1) ; => 21105
  (solve input :part 2) ; => 5329815
  )
