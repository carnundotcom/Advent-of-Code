(ns y2024.d4
  (:require
    [clojure.core.matrix :as m]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input ["MMMSXXMASM"
                  "MSAMXMSMSA"
                  "AMXSXMAAMM"
                  "MSAMASMSMX"
                  "XMASAMXAMM"
                  "XXAMMXXAMA"
                  "SMSMSASXSS"
                  "SAXAMASAAA"
                  "MAMMMXMMMM"
                  "MXMXAXMASX"])
(def input (u/input))

; ----------------------------------------------------------------------------------------------------------------------

(defn part-1
  [input]
  (let [horizontal  input
        vertical    (apply map str input)
        char-grid   (mapv #(mapv identity %) input)
        dirg-rahc   (mapv reverse char-grid)
        left-diags  (->> (for [i (range (- (dec (count char-grid)))
                                        (count char-grid))]
                           (m/diagonal char-grid i))
                         (mapv (partial apply str)))
        right-diags (->> (for [i (range (- (dec (count (first dirg-rahc))))
                                        (count (first dirg-rahc)))]
                           (m/diagonal dirg-rahc i))
                         (mapv (partial apply str)))]
    (->> (concat horizontal vertical left-diags right-diags) ; a list of strings
         (mapcat #(re-seq #"(?=(XMAS|SAMX))" %))             ; all matches
         (count))))


(defn part-2
  [input]
  (let [char-grid       (mapv #(mapv identity %) input)
        three-by-threes (for [col (range 0 (- (count (first char-grid)) 2))
                              row (range 0 (- (count char-grid) 2))]
                          (m/matrix (m/submatrix char-grid row 3 col 3)))
        match?          (fn [[[aa _  ac]
                              [_  bb _ ]
                              [ca _  cc]]]
                          (and (or (= [aa bb cc] [\M \A \S])
                                   (= [aa bb cc] [\S \A \M]))
                               (or (= [ac bb ca] [\M \A \S])
                                   (= [ac bb ca] [\S \A \M]))))]
    (->> three-by-threes
         (map #(if (match? %) 1 0))
         (apply +))))

; ----------------------------------------------------------------------------------------------------------------------

(tests
  (part-1 dummy-input) := 18
  (part-2 dummy-input) := 9
  )

(comment
  (part-1 input) ; => 2642
  (part-2 input) ; => 1974
  )
