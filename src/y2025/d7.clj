(ns y2025.d7
  (:require
    [clojure.set :as set]
    [hyperfiddle.rcf :refer [tests]]
    [utils :as u]))

(def dummy-input
  [".......S......."
   "..............."
   ".......^......."
   "..............."
   "......^.^......"
   "..............."
   ".....^.^.^....."
   "..............."
   "....^.^...^...."
   "..............."
   "...^.^...^.^..."
   "..............."
   "..^...^.....^.."
   "..............."
   ".^.^.^.^.^...^."
   "..............."])
(def input (u/input))

(defn parse [input]
  (mapv vec input))

; ---

(defn solve [input]
  (let [[first-row & rest-rows] (parse input)]
    ; Outer reduction: one row at a time
    (reduce (fn [m row]
              ; Inner reduction: one beam index at a time, considering only indices that have beams entering them
              (reduce (fn [m [i paths]]
                        (let [ch (get row i)]
                          (if (= \. ch) m
                            (-> m
                                (update :i->paths #(as-> % m
                                                     ; 'forget' beam at current position, then create two new beams:
                                                     ;   one each side of the splitter
                                                     (dissoc m i)
                                                     (merge-with + ; <-- (handle beam overlaps: *add* paths)
                                                       m {(dec i) paths, (inc i) paths})))
                                (update :splitters inc)))))
                      m
                      (:i->paths m)))

            ; Keep track of horizontal beam positions (i.e. indices in row vectors), with number of ways to get there
            ; (i.e. 'paths'); and number of splitters encountered.
            {:i->paths {(get (set/map-invert first-row) \S) 1}, :splitters 0}

            rest-rows)))

(defn part-1 [input]
  (:splitters (solve input)))

(defn part-2 [input]
  (apply + (vals (:i->paths (solve input)))))

(tests
  (part-1 dummy-input) := 21
  (part-2 dummy-input) := 40
  )

(comment
  (part-1 input) ; => 1642
  (part-2 input) ; => 47274292756692

  )
