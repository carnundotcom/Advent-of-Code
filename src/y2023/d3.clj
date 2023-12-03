(ns y2023.d3
  (:require [clojure.set :as set]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["467..114.."
                  "...*......"
                  "..35..633."
                  "......#..."
                  "617*......"
                  ".....+.58."
                  "..592....."
                  "......755."
                  "...$.*...."
                  ".664.598.."])
(def input (u/input))

;;; --- input parsing ---

(defn- char->type [c]
  (cond
    (Character/isDigit c) :digit
    (= \. c) nil
    :else :symbol))

(defn- parse-row [s y]
  (-> (partition-by char->type s)
      ; e.g. => ((\4 \6 \7) (\. \.) (\1 \1 \4) (\. \.))
      (->> (reduce (fn [{:keys [x] :as m} [c :as cs]]
                     (let [t (char->type c)]
                       (cond-> m
                         (= :digit t)
                         ; parse the number and note all coords
                         (update :n+coords conj [(parse-long (apply str cs))
                                                 (set (for [x (range x (+ x (count cs)))] [x y]))])

                         (= :symbol t)
                         ; note coord of symbol
                         (update :x+y->sym assoc [x y] c)

                         :always
                         ; 'jump' x ahead according to length of run
                         (update :x + (count cs)))))
                   {:x 0
                    :n+coords []
                    :x+y->sym {}}))
      ; don't return x — it's an implementation detail
      (dissoc :x)))

(defn parse
  "Returns a map of the form

    {:n+coords [<int> #{<coord> ...} ...]
     :x+y->sym {<coord> <symbol char>, ...}}

  where :n+coords represents the multiple coordinates of each number in the grid, and :x+y->sym the coordinates of
  'symbol' characters."
  [input]
  (let [rows (map-indexed (fn [y row] (parse-row row y)) input)]
    ; combine data across rows
    {:n+coords (vec (mapcat :n+coords rows))
     :x+y->sym (into {} (mapcat :x+y->sym rows))}))

;;; --- solving ---

(defn- adj-set
  "Returns a set of the coordinates adjacent to [x y]."
  [input [x y]]
  (let [x-max (count (first input))
        y-max (count input)]
    (set
      (for [xd (range -1 2)
            yd (range -1 2)
            :let [x (+ x xd)
                  y (+ y yd)]
            :when (and (or (not= 0 xd) (not= 0 yd))
                       (< x x-max)
                       (< y y-max))]
        [x y]))))

(defn solve [input & {:keys [part]}]
  (let [{:keys [n+coords x+y->sym]} (parse input)]
    ; reduce over map of 'symbol' characters
    (reduce (fn [sum [[x y] _sym]]
              (let [adj-set (adj-set input [x y])
                    ; list all numbers adjacent to sym
                    nums (keep (fn [[n coords]]
                                 ; (a number is adjacent if adj-set intersecs with its coords)
                                 (when (seq (set/intersection adj-set coords))
                                   n))
                               n+coords)]

                (case part
                  ; sum all symbol-adjacent numbers
                  1 (apply + sum nums)
                  ; sum only (the products of) symbol-adjacent *pairs* of numbers
                  2 (if (= 2 (count nums))
                      (+ sum (apply * nums))
                      sum))))

            0 x+y->sym)))

(tests
  (solve dummy-input :part 1) := 4361
  (solve dummy-input :part 2) := 467835
  )

(comment
  (solve input :part 1) ; => 514969
  (solve input :part 2) ; => 78915902
  )
