(ns y2022.d5
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["    [D]    "
                  "[N] [C]    "
                  "[Z] [M] [P]"
                  " 1   2   3 "
                  ""
                  "move 1 from 2 to 1"
                  "move 3 from 1 to 3"
                  "move 2 from 2 to 1"
                  "move 1 from 1 to 2"])
(def input (u/input))

; ---

; stacks of crates as lists — 'top' of stack is first in list
; parsing needs to produce the lists and a sequence of drops and conjes — perhaps with a macro?

(defn- uppercase-letter? [ch]
  (when (and (<= (int \A) (int ch) (inc (int \Z)))
             (not= ch \[) (not= ch \]))
    ch))

(tests
  (uppercase-letter? \A) := \A
  (uppercase-letter? \Z) := \Z
  (uppercase-letter? \G) := \G
  (uppercase-letter? \a) := nil
  (uppercase-letter? \space) := nil
  (uppercase-letter? \[) := nil)

(defn parse [input]
  (let [{stacks false, steps true} (group-by #(str/starts-with? % "move") input)
        stacks (->> (take (- (count stacks) 2) stacks) ; ignore numbers and blank line
                    (map #(partition 4 4 (repeat \space) %))
                    (apply (partial map list))
                    (mapv #(keep (partial some uppercase-letter?) %)))]
    {:stacks stacks
     :steps (map #(let [[move from to] (re-seq #"\d+" %)]
                    [(parse-long move)
                     ;; 0-based indexing of the stacks vector
                     (dec (parse-long from))
                     (dec (parse-long to))]) steps)}))

(defn- single-move
  "Part 1 helper."
  [stacks from to]
  (let [[crate] (get stacks from)]
    (-> stacks
        (update from rest)
        (update to conj crate))))

(defn moves [stacks [n from to] & {:keys [part] :or {part :part-1}}]
  (condp = part
    :part-1 (loop [n n, stacks stacks]
              (if (< 0 n)
                (recur (dec n) (single-move stacks from to))
                stacks))
    :part-2 (let [[crates from-crates] (split-at n (get stacks from))]
              (-> stacks
                  (assoc from from-crates)
                  (assoc to (concat crates (get stacks to)))))))

(defn rearrange-crates [input part]
  (let [{:keys [stacks steps]} (parse input)]
    (->> (reduce #(moves %1 %2 :part part) stacks steps)
         (map first)
         (apply str))))

(tests
  (rearrange-crates dummy-input :part-1) := "CMZ"
  (rearrange-crates dummy-input :part-2) := "MCD")

(comment
  (rearrange-crates input :part-1) ; => "VGBBJCRMN"
  (rearrange-crates input :part-2) ; => "LBBVJBRMH"
  )
