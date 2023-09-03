(ns y2015.d4
  (:require [hyperfiddle.rcf :refer [tests]]
            [utils :as u]))


(def input (first (u/input)))

; ---

(do (defn chug-lazy
      "Chugs through hashes until one is found that starts with the specified number of zeros."
      [s n]
      (let [found? #(= (subs (u/md5 (str s %)) 0 n)
                       (apply str (repeat n \0)))]
        (some #(when (found? %) %)
              (range 1 ##Inf))))
    (time (chug-lazy input 5)))

(do (defn chug-imperative
      "Chugs through hashes until one is found that starts with the specified number of zeros."
      [s n]
      (let [found? #(= (subs (u/md5 (str s %)) 0 n)
                       (apply str (repeat n \0)))]
        (loop [salt 1]
          (if (found? salt)
            salt
            (recur (inc salt))))))
    (time (chug-imperative input 5)))


(tests
  (subs (u/md5 "abcdef609043") 0 5) := "00000"
  )

(comment

  (time (chug-lazy input 5))
  ;;="Elapsed time: 496.611692 msecs"
  ;;282749

  (time (chug-lazy input 6))
  ;;="Elapsed time: 17432.438005 msecs"
  ;9962624
  )
