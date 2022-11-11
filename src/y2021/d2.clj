(ns y2021.d2
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["forward 5" "down 5" "forward 8" "up 3" "down 8" "forward 2"])
(def input (u/input))

; ---

(defn parse-command [command]
  (let [[d v] (str/split command #" ")
        [sign axis] (get {"forward" [1 :x] , "down" [1 :z] , "up" [-1 :z]} d)
        n (parse-long v)]
    (assoc {} axis (* sign n))))

(defn part-1 [input]
  (->> input
       (reduce (fn [m command]
                 (merge-with + m (parse-command command)))
               {})
       vals
       (apply *)))

(tests
  (parse-command "forward 5") := {:x 5}
  (parse-command "down 8") := {:z 8}
  (parse-command "up 3") := {:z -3}

  (part-1 dummy-input) := 150)

(comment
  (part-1 input) ; => 2150351
  )

; ---

(defn parse-command-2 [command aim]
  (let [[d v] (str/split command #" ")
        x (parse-long v)
        diff (get {"down" {:aim x}
                   "up" {:aim (- x)}
                   "forward" {:x x, :z (* aim x)}}
                  d)]
    diff))

(defn part-2 [input]
  (->> (reduce (fn [{:keys [aim] :as m} command]
                 (let [diff (parse-command-2 command aim)]
                   (merge-with + m diff)))
               {:aim 0 :x 0 :z 0}
               input)
       ((juxt :x :z))
       (apply *)))

(tests
  (part-2 dummy-input) := 900)

(comment
  (part-2 input) ; => 1842742223
  )
