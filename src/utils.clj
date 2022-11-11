(ns utils
  (:require [clojure.java.io :as io]))

(defn read-string-seq [filename]
 (with-open [r (io/reader (str "data/" filename ".txt"))]
                    (doall (line-seq r))))
