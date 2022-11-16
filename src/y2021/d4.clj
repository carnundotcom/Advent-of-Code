(ns y2021.d4
  (:require [clojure.set :refer [map-invert]]
            [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1"
                  ""
                  "22 13 17 11  0"
                  " 8  2 23  4 24"
                  "21  9 14 16  7"
                  " 6 10  3 18  5"
                  " 1 12 20 15 19"
                  ""
                  " 3 15  0  2 22"
                  " 9 18 13 17  5"
                  "19  8  7 25 23"
                  "20 11 10 24  4"
                  "14 21 16 12  6"
                  ""
                  "14 21 17 24  4"
                  "10 16 15  9 19"
                  "18  8 23 26 20"
                  "22 11 13  6  5"
                  " 2  0 12  3  7"])
(def input (u/input))

; --- input parsing ---

(defn- parse-board
  "Produces a val -> {:x, :y} map."
  [board]
  (->> board
       (map-indexed (fn [x row]
                      (map-indexed (fn [y v]
                                     [v {:x x :y y}])
                                   row)))
       (apply concat)
       (into {})))

(defn parse-input [[draws _ & boards]]
  (let [boards (->> (partition-by str/blank? boards)
                    (remove #(= '("") %))
                    (mapv (fn [board]
                            (->> board
                                 (map #(->> (str/split % #"\s+")
                                            (remove str/blank?)
                                            (map parse-long)))
                                 parse-board))))]
    {:draws (->> (str/split draws #",")
                 (map parse-long))
     :val-boards boards
     :pos-boards (mapv map-invert boards)}))

(tests
  (def parsed-dummy-input (parse-input dummy-input))
  (:draws parsed-dummy-input) := '(7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1)
  (get-in parsed-dummy-input [:val-boards 0 22]) := {:x 0 :y 0}
  (get-in parsed-dummy-input [:val-boards 0 18]) := {:x 3 :y 3}
  (get-in parsed-dummy-input [:val-boards 0 5]) := {:x 3 :y 4}
  (get-in parsed-dummy-input [:pos-boards 0 {:x 0 :y 0}]) := 22
  (get-in parsed-dummy-input [:pos-boards 0 {:x 3 :y 3}]) := 18
  (get-in parsed-dummy-input [:pos-boards 0 {:x 3 :y 4}]) := 5
  )

; --- part 1 ---

(defn- winner? [val-board pos-board [x y]]
  (let [marked-at-pos? (fn [val-board pos-board x y]
                         (get-in val-board [(get pos-board {:x x :y y}) :marked?]))
        board-size (int (Math/sqrt (count val-board)))
        row-marks (-> (for [col (range board-size)]
                        (marked-at-pos? val-board pos-board x col)))
        col-marks (-> (for [row (range board-size)]
                        (marked-at-pos? val-board pos-board row y)))]
    (or (every? true? row-marks)
        (every? true? col-marks))))

(defn- maybe-mark-board [{:keys [val-boards pos-boards] :as m} n draw]
  (let [val-board (get val-boards n)
        [val-board' [x y]] (let [{:keys [x y]} (get val-board draw)]
                             (if x
                               [(assoc-in val-board [draw :marked?] true) [x y]]
                               [val-board]))]
    (cond-> (assoc-in m [:val-boards n] val-board')
      (and x (winner? val-board' (get pos-boards n) [x y]))
      (assoc :winning-val-board val-board'))))

(defn- mark-boards [{:keys [val-boards] :as m} draw]
  (loop [n (dec (count val-boards))
         m' m]
    (if (< n 0)
      m'
      (recur (dec n)
             (maybe-mark-board m' n draw)))))

(defn make-draws [{:keys [draws] :as parsed-input}]
  (loop [draws' draws
         m (select-keys parsed-input [:val-boards :pos-boards])]
    (let [[draw & draws'] draws'
          m' (and draw (mark-boards m draw))
          winning-board (and draw (:winning-val-board m'))]
      (cond
        (not draw) m
        winning-board {:winning-draw draw
                       :winning-board winning-board}
        :else (recur draws' m')))))

(defn score [{:keys [winning-draw winning-board]}]
  (* winning-draw
     (->> (remove (comp :marked? second) winning-board)
          (map first)
          (reduce +))))

(defn part-1 [input]
  (-> (parse-input input)
      make-draws
      score))

(tests
  (part-1 dummy-input) := 4512)

(comment
  (part-1 input) ; => 10680
  )
