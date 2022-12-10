(ns y2022.d10
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def inputs (->> (partition-by (partial = "") (u/input)) (remove (partial = '("")))))
(def dummy-input-1 (first inputs))
(def dummy-input-2 (second inputs))
(def input (nth inputs 2))

; ---

(defn cycles [input]
  (reduce (fn [cycles line]
            (let [[instruction v] (str/split line #" ")
                  v (and v (parse-long v))]
              (condp = instruction
                "noop" (conj cycles (peek cycles))
                "addx" (conj cycles (peek cycles) (+ (peek cycles) v)))))
          [1] input))

(defn signal-strengths [cycles cycle-nums]
  (reduce (fn [sum cycle-num]
            (+ sum (* cycle-num (get cycles (dec cycle-num)))))
          0 cycle-nums))

(defn part-1 [input]
  (-> (cycles input)
      (signal-strengths '(20 60 100 140 180 220))))

(defn part-2 [input]
  (->> (cycles input)
       (map-indexed (fn [crt-pos sprite-pos]
                      (let [crt-pos (mod crt-pos 40)]
                        (if (<= (dec sprite-pos) crt-pos (inc sprite-pos)) "#" "."))))
       (partition 40)
       (map #(apply str %))))

(tests
  (part-1 dummy-input-2) := 13140
  (part-2 dummy-input-2) :=
  '("##..##..##..##..##..##..##..##..##..##.."
    "###...###...###...###...###...###...###."
    "####....####....####....####....####...."
    "#####.....#####.....#####.....#####....."
    "######......######......######......####"
    "#######.......#######.......#######....."))

(comment
  (part-1 input) ; => 17840
  (part-2 input) ; =>
  '("####..##..#.....##..#..#.#....###...##.."
    "#....#..#.#....#..#.#..#.#....#..#.#..#."
    "###..#..#.#....#....#..#.#....#..#.#...."
    "#....####.#....#.##.#..#.#....###..#.##."
    "#....#..#.#....#..#.#..#.#....#....#..#."
    "####.#..#.####..###..##..####.#.....###."))
