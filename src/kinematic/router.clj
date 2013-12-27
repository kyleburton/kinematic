(ns kinematic.router
  (:use
   [clj-etl-utils.lang-utils :only [raise]]))


(comment

  (defapp :my-app
    :mount-point "/my-app"
    :before      [#_"seq of kinematic handlers"]
    :after       [#_"seq of kinematic handlers"])


  (api/register :my-app ["/index"]
                :before [#_"seq of kinematic handlers"]
                :after  [#_"seq of kinematic handlers"])


  )

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

(defn pattern-matches-uri? [^String mount-point ^String pattern ^String uri]
  (let [uri           (substr uri (count mount-point))
        uri           (if (.startsWith uri "/")
                        (substr uri 1)
                        uri)
        pattern-parts (.split pattern "/")
        uri-parts     (.split uri     "/")]
    (if (not (= (count pattern-parts)
                (count uri-parts)))
      {:matched      false
       :route-params {}
       ;; :reason (format "Count mismatch: %s vs %s"
       ;;                 (count pattern-parts)
       ;;                 (count uri-parts))
       }
      (loop [[pp & pattern-parts] pattern-parts
             [up & uri-parts]     uri-parts
             route-params         {}]
        ;; did we run out of uri-parts and pattern-parts?
        (cond
          (and (not pp)
               (not up))
          {:matched      true
           :route-params route-params}

          ;; if a parameter, it's a match
          (.startsWith pp ":")
          (recur pattern-parts uri-parts (assoc route-params (keyword (substr pp 1))
                                                up))

          (= pp up)
          (recur pattern-parts uri-parts route-params)

          :otherwise
          {:matched      false
           :route-params route-params
           ;; :reason       "Fell through."
           })))))
