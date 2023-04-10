(ns y2022.d14
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["498,4 -> 498,6 -> 496,6"
                  "503,4 -> 502,4 -> 502,9 -> 494,9"])
(def input (u/input))
(def sand-origin [500 0])

; ---

(defn paths [input]
  (map #(->> (str/split % #" -> |,")
             (map parse-long)
             (partition 2))
       input))

(defn points-set [paths]
  (->> (mapcat (fn [path]
                 (->> (partition 2 1 path)
                      (reduce (fn [points [[x1 y1] [x2 y2]]]
                                (into points
                                      (for [x (range (min x1 x2) (inc (max x1 x2)))
                                            y (range (min y1 y2) (inc (max y1 y2)))]
                                        [x y])))
                              #{})))
               paths)
       (into #{})))

(defn bounds [points]
  (reduce (fn [[_ s e w] [x y]]
            (cond
              (> y s) [_ y e w]
              (> x e) [_ s x w]
              (< x w) [_ s e x]
              :else [_ s e w]))
          [0 ##-Inf ##-Inf ##Inf]
          points))


(defn print-scan
  ([points] (print-scan points {:at-rest? #{}}))
  ([points {:keys [moving at-rest?] :as _sand}]
   (let [[_n s e w] (bounds points)]
     (dotimes [y (inc s)]
       (println
         (->> (for [x (range w (inc e))]
                (cond
                  (points [x y]) \#
                  (at-rest? [x y]) \0
                  (= moving [x y]) \o
                  :else \.))
              (apply str))))
     (println "\n"))))


(defn step-sand
  ([points] (step-sand points {:at-rest? #{}}))
  ([points sand] (step-sand points sand nil))
  ([points
    {at-rest? :at-rest?
     [x y :as moving] :moving
     :as sand}
    floor] ; part-2
   (if-not moving
     (assoc sand :moving [500 0])
     (let [[down down-left down-right] [[x (inc y)] [(dec x) (inc y)] [(inc x) (inc y)]]
           step (and
                  ;; part-2
                  (or (not floor) (< (inc y) floor))
                  ;; part-1
                  (or (and (not (points down)) (not (at-rest? down)) down)
                      (and (not (points down-left)) (not (at-rest? down-left)) down-left)
                      (and (not (points down-right)) (not (at-rest? down-right)) down-right)))]
       (if step
         (assoc sand :moving step)
         (-> (update sand :at-rest? conj moving)
             (dissoc :moving)))))))


(defn solve [input & {:keys [print? print-final? part] :or {part 1}}]
  (let [points? (points-set (paths input))
        [_n s _e _w] (bounds points?)]
    (loop [{:keys [at-rest?] :as sand} (step-sand points?)]
      (when print? (print-scan points? sand))
      (if (or (and (= 1 part) (some-> (:moving sand) second (> s))) ; second = y
              (and (= 2 part) (at-rest? [500 0])))
        (do
          (when print-final? (print-scan points? sand)) ; FIXME: bounds need to be recalculated at the end
          (count (:at-rest? sand)))
        (recur (step-sand points? sand (when (= 2 part) (+ 2 s))))))))


(tests
  (solve dummy-input {:print-final? true}) := 24
  (solve dummy-input {:part 2, :print-final? true}) := 93
  )

(comment
  (solve input) ; => 817
  (solve input {:part 2}) ; => 23416
  )
