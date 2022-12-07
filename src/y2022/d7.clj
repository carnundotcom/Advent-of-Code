(ns y2022.d7
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [clojure.zip :as zip]
            [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))

(def dummy-input ["$ cd /"
                  "$ ls"
                  "dir a"
                  "14848514 b.txt"
                  "8504156 c.dat"
                  "dir d"
                  "$ cd a"
                  "$ ls"
                  "dir e"
                  "29116 f"
                  "2557 g"
                  "62596 h.lst"
                  "$ cd e"
                  "$ ls"
                  "584 i"
                  "$ cd .."
                  "$ cd .."
                  "$ cd d"
                  "$ ls"
                  "4060174 j"
                  "8033020 d.log"
                  "5626152 d.ext"
                  "7214296 k"])
(def input (u/input))

; ---

(defn dispatch-parse-line* [_loc [a b & _args]]
  (cond
    (= "$" a) [:command b]
    (= "dir" a) [:dir]
    :else [:file]))

(defmulti parse-line* #'dispatch-parse-line*)

(defmethod parse-line* :default [loc split-line]
  (prn "no impl for:" split-line)
  loc)

(defn- move-into-dir [loc dirname]
  (let [node (zip/node loc)]
    (if (and (vector? node) (= dirname (:name (first node))))
      loc
      (move-into-dir (zip/right loc) dirname))))

(defmethod parse-line* [:command "cd"] [loc [_ _ dirname]]
  (condp = dirname
    "/" loc
    ".." (zip/up loc)
    (-> (zip/down loc)
        (move-into-dir dirname))))

(defmethod parse-line* [:command "ls"] [loc _]
  ;; ignore
  loc)

(defmethod parse-line* [:dir] [loc [_ dirname]]
  (zip/append-child loc [{:name dirname, :type :dir}]))

(defmethod parse-line* [:file] [loc [size filename]]
  (zip/append-child loc {:name filename, :type :file, :size (parse-long size)}))

(defn parse-line [loc line]
  (parse-line* loc (str/split line #" ")))

(defn parse [input]
  (loop [loc (zip/vector-zip [{:name "/", :type :dir}])
         i 0]
    (if (< i (count input))
      (recur (parse-line loc (get input i)) (inc i))
      (zip/root loc))))

(tests
  (parse dummy-input)
  := [{:name "/", :type :dir}
      [{:name "a", :type :dir}
       [{:name "e", :type :dir}
        {:name "i", :type :file, :size 584}]
       {:name "f", :type :file, :size 29116}
       {:name "g", :type :file, :size 2557}
       {:name "h.lst", :type :file, :size 62596}]
      {:name "b.txt", :type :file, :size 14848514}
      {:name "c.dat", :type :file, :size 8504156}
      [{:name "d", :type :dir}
       {:name "j", :type :file, :size 4060174}
       {:name "d.log", :type :file, :size 8033020}
       {:name "d.ext", :type :file, :size 5626152}
       {:name "k", :type :file, :size 7214296}]])

(defn assoc-dir-sizes [tree]
  (letfn [(assoc-sub-sizes [form]
            (if-let [_dir? (and (vector? form) (= :dir (:type (first form))))]
              (assoc-in form [0 :size]
                        (reduce (fn [size child]
                                  (+ size (or (:size child)
                                              (:size (first child)))))
                                0 (rest form)))
              form))]
    (walk/postwalk assoc-sub-sizes tree)))

(defn part-1 [input]
  (->> (parse input)
       assoc-dir-sizes
       flatten
       (keep #(when (and (= :dir (:type %))
                         (>= 100000 (:size %)))
                (:size %)))
       (apply +)))

(defn part-2 [input]
  (let [total 70000000
        target 30000000
        dirs (-> (parse input)
                 assoc-dir-sizes
                 flatten)
        unused (- total (:size (first dirs)))]
    (->> dirs
         (keep #(when (and (= :dir (:type %))
                          (> (+ unused (:size %)) target))
                     (:size %)))
         (apply min))))

(tests
  (part-1 dummy-input) := 95437
  (part-2 dummy-input) := 24933642)

(comment
  (part-1 input) ; => 1084134
  (part-2 input) ; => 6183184
  )
