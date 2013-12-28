(ns kinematic.util)

(defn substr
  ([^String s start]
     (cond
       (nil? s)
       ""
       (> start (count s))
       ""
       :ok
       (.substring s start)))
  ([^String s start end]
     (cond
       (nil? s)
       ""
       (> start (count s))
       ""

       (> end (count s))
       (.substring s start)

       :ok
       (.substring s start end))))



