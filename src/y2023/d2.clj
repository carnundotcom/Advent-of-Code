(ns y2023.d2
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green"
                  "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue"
                  "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red"
                  "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red"
                  "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green"])
(def input (u/input))

;;; --- input parsing ---

(defn- parse-reveals
  [reveals]
  (mapv
    (fn [s]
      (->> (str/split s #", ")
           (map #(str/split % #" "))
           (map (fn [[n c]] [c (parse-long n)]))
           (into {})))
    reveals))

(defn- parse-line
  [s]
  (let [[id & reveals] (re-seq #"(?<=Game )\d+|(?<=: |; )[^;]+" s)]
    {:id (parse-long id)
     :cubes (parse-reveals reveals)}))

(defn parse [input]
  (mapv parse-line input))

(comment
  (parse-reveals '("3 blue, 4 red" "1 red, 2 green, 6 blue" "2 green"))
  ; => [{"blue" 3, "red" 4} {"red" 1, "green" 2, "blue" 6} {"green" 2}]

  (parse-line (first dummy-input))
  ; => {:id 1, :cubes [{"blue" 3, "red" 4} {"red" 1, "green" 2, "blue" 6} {"green" 2}]}

  (parse dummy-input)
  ; => [{:id 1, :cubes [{"blue" 3, "red" 4} {"red" 1, "green" 2, "blue" 6} {"green" 2}]}
  ;     {:id 2, :cubes [{"blue" 1, "green" 2} {"green" 3, "blue" 4, "red" 1} {"green" 1, "blue" 1}]}
  ;     {:id 3, :cubes [{"green" 8, "blue" 6, "red" 20} {"blue" 5, "red" 4, "green" 13} {"green" 5, "red" 1}]}
  ;     {:id 4, :cubes [{"green" 1, "red" 3, "blue" 6} {"green" 3, "red" 6} {"green" 3, "blue" 15, "red" 14}]}
  ;     {:id 5, :cubes [{"red" 6, "blue" 1, "green" 3} {"blue" 2, "red" 1, "green" 2}]}]
  )

;;; --- solving ---

;; part 1
(defn possible? [game]
  (let [c->max {"red" 12, "green" 13, "blue" 14}]
    ;; for every selection in this game...
    (every? #(every? (fn [c] ; ... every color <= max (or absent)
                       (<= (get % c 0) (get c->max c)))
                     (keys c->max))
            (:cubes game))))

;; part 2
(defn min-cubes [game]
  ;; reduce over all 'cubes' (selections of cubes) in this game...
  (reduce (fn [m cube]
            (->> m
                 ;; ... to produce a map of min required for each color
                 (map (fn [[c n]] [c (max n (get cube c 0))]))
                 (into {})))
          {"red" 0
           "green" 0
           "blue" 0}
          (:cubes game)))

(defn solve [input & {:keys [part]}]
  (->> (parse input)
       (keep (fn [game]
               (case part
                 1 (when (possible? game)
                     (:id game))
                 2 (->> (min-cubes game)
                        (vals)
                        (apply *)))))
       (apply +)))


(tests
  (mapv possible? (parse dummy-input)) := [true true false false true]
  (mapv min-cubes (parse dummy-input)) := [{"red" 4, "green" 2, "blue" 6}
                                           {"red" 1, "green" 3, "blue" 4}
                                           {"red" 20, "green" 13, "blue" 6}
                                           {"red" 14, "green" 3, "blue" 15}
                                           {"red" 6, "green" 3, "blue" 2}]

  (solve dummy-input :part 1) := 8
  (solve dummy-input :part 2) := 2286
  )

(comment
  (solve input :part 1) ; => 2512
  (solve input :part 2) ; => 67335
  )
