(ns utils
  (:require [babashka.fs :as fs]
            [clojure.string :as str])
  (:import java.security.MessageDigest
           java.math.BigInteger))


;; --- input ---

(defn- input-filename [ns]
  (->> (str/split (str ns) #"\.")
       (apply format "data/%s/%s.txt")))

(defn input
  "By default, reads all lines from the puzzle input into a vector of strings.

  Options:
   - :ns --> Puzzle namespace, from which the puzzle input filename is derived. Defaults to calling namespace.
   - :as --> What to return. Defaults to a vector of strings (:lines-v). Also available:
              - :grid --> 2D vector of characters"
  [& {:keys [ns as], :or {ns *ns*, as :lines-v}, :as _opts}]
  (let [lines-v (fs/read-all-lines (input-filename ns))]
    (case as
      :lines-v lines-v
      :grid    (mapv #(mapv identity %) lines-v)

      "invalid :as")))


;; --- sequences ---

(defn some-i
  "Returns the index of the first item where (f item) is truthy, else nil."
  [f coll]
  (first (keep-indexed #(when (f %2) %1) coll)))


;; --- grids ---

(def dirs [:n :e :s :w])

(def dir->x+y
  {:n [0 -1]
   :e [1  0]
   :s [0  1]
   :w [-1 0]})

(def turn-right
  {:n :e
   :e :s
   :s :w
   :w :n})

(def turn-left
  {:n :w
   :e :n
   :s :e
   :w :s})

(def turn-around
  {:n :s
   :e :w
   :s :n
   :w :e})

(defn adj-positions
  "Returns a lazy sequence of the (up to) 8 positions immediately adjacent to `pos`."
  [grid [row col :as _pos]]
  (for [x (range -1 2)
        y (range -1 2)
        :let [row' (+ row x)
              col' (+ col y)]
        :when (and (not (and (zero? x) (zero? y)))
                   (not (or (< row' 0) (< col' 0)))
                   (not (or (>= row' (count (first grid))) (>= row' (count grid)))))]
    [row' col']))

(defn grid->positions
  "Returns a lazy sequence of all [row col] positions in the 'grid' (a 2D vector)."
  [grid]
  (for [col (range (count (first grid)))
        row (range (count grid))]
    [row col]))

(defn pos-in-grid
  "Returns the first [x y] position in the 'grid' (a 2D vector or similar) for which (pred <element at position>)
  is truthy, else nil."
  [grid pred]
  (first (keep-indexed
           (fn [y row]
             (first (keep-indexed
                      (fn [x ch]
                        (when (pred ch)
                          [x y]))
                      row)))
           grid)))


;; --- hashing ---

;; with thanks to https://gist.github.com/jizhang/4325757?permalink_comment_id=2196746#gistcomment-2196746
(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))
