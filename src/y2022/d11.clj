(ns y2022.d11
  (:require [clojure.string :as str]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["Monkey 0:"
                  "  Starting items: 79, 98"
                  "  Operation: new = old * 19"
                  "  Test: divisible by 23"
                  "    If true: throw to monkey 2"
                  "    If false: throw to monkey 3"
                  ""
                  "Monkey 1:"
                  "  Starting items: 54, 65, 75, 74"
                  "  Operation: new = old + 6"
                  "  Test: divisible by 19"
                  "    If true: throw to monkey 2"
                  "    If false: throw to monkey 0"
                  ""
                  "Monkey 2:"
                  "  Starting items: 79, 60, 97"
                  "  Operation: new = old * old"
                  "  Test: divisible by 13"
                  "    If true: throw to monkey 1"
                  "    If false: throw to monkey 3"
                  ""
                  "Monkey 3:"
                  "  Starting items: 74"
                  "  Operation: new = old + 3"
                  "  Test: divisible by 17"
                  "    If true: throw to monkey 0"
                  "    If false: throw to monkey 1"])
(def input (u/input))

; ---

(defn- parse-op [op-str]
  (let [[_ _ _ _ _ a op b] (str/split op-str #" ")]
    (fn [x]
      (({"*" *, "+" +} op)
       (bigint (if (= "old" a) x (parse-long a)))
       (bigint (if (= "old" b) x (parse-long b)))))))

(defn- parse-test [divisor-str true-str false-str]
  (let [get-n #(parse-long (re-find #"\d+" %))]
    (fn [x]
      (if (zero? (mod x (get-n divisor-str)))
        (get-n true-str)
        (get-n false-str)))))

(defn parse [input]
  (->> (partition-by #(= "" %) input)
       (remove #(= '("") %))
       (mapv (fn [[_ starting op test-divisor test-true test-false]]
              {:items (mapv parse-long (re-seq #"(?<= )\d+" starting))
               :op (parse-op op)
               :test (parse-test test-divisor test-true test-false)
               :inspections 0}))))

; ---

(defn turn [monkeys id part]
  (let [{:keys [op test items]} (get monkeys id)]
    (reduce (fn [monkeys item]
              (let [item' (cond-> (op item)
                            (= :part-1 part) (-> (/ 3) int))
                    throw-to (test item')]
                (update-in monkeys [throw-to :items] conj item')))
            (-> (assoc-in monkeys [id :items] [])
                (update-in [id :inspections] + (count items)))
            items)))

(defn round [monkeys part]
  (reduce (fn [monkeys id]
            (turn monkeys id part))
          monkeys
          (range (count monkeys))))

(defn rounds [monkeys n part]
  (nth (iterate #(round % part) monkeys) n))

(defn score [monkeys]
  (let [[{a :inspections} {b :inspections}] (sort-by :inspections #(compare %2 %1) monkeys)]
    (* (bigint a) (bigint b))))

(defn solve [input part]
  (-> (parse input)
      (rounds (case part :part-1 20 :part-2 1000) part) ; FIXME: 10k, not 1000
      score))

(tests
  (-> (parse dummy-input) (rounds 1 :part-1) (->> (map :items))) := '([20 23 27 26] [2080 25 167 207 401 1046] [] [])
  (-> (parse dummy-input) (rounds 20 :part-1) (->> (map :items))) := '([10 12 14 26 34] [245 93 53 199 115] [] [])
  (solve dummy-input :part-1) := 10605N
  ;(time (solve dummy-input :part-2)) ; off the charts already... D:
  )

(comment
  (solve input :part-1) ; => 78678N
  (solve input :part-2) ; => ???
  )
