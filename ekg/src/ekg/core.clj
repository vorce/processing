(ns ekg.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; https://www.desmos.com/calculator/w9jrdpvsmk?lang=sv-SE
(defn sinusoid [y amp yoffset freq xoffset]
  (+ (* amp (q/sin (/ (- y yoffset) freq))) xoffset))

; 100 0 50.0 620
(defn sinusoid-point [amp yoffset freq xoffset p]
  (assoc p :x (sinusoid (:y p) amp yoffset freq xoffset)))

(defn coord-gen [xoffset maxy]
  (for [y (range maxy)]
    {:x xoffset,
     :y y}))

(defn perlin [p]
  (let [x (:x p)
        y (:y p)
        strength 300]
    (assoc p
           :x (q/map-range (q/noise x y) 0 1 (- x strength) (+ x strength))
           ;:y (q/map-range (q/noise x y) 0 1 (- y 20) (+ y 20))
           )))

(defn slope [p1 p2]
  ;(println p1 ", " p2)
  (let [x1 (:x p1)
        y1 (:y p1)
        x2 (:x p2)
        y2 (:y p2)
        xdivisor (- x2 x1)]
    (if (zero? xdivisor)
      0
      (/ (- y2 y1) xdivisor))))

(defn perpendicular-slope [m]
  (if (zero? m)
    m
    (/ -1 m)))

; y − p2.y = slope * (x − p2.x)
; 
(defn perpendicular-point [p1 p2 distance direction]
  (let [m (slope p1 p2)
        perp-m (perpendicular-slope m)
        angle (q/atan perp-m)
        ;direction (if (zero? (rand-int 2)) -1 1)
        new-x (+ (:x p2) (* direction distance (q/cos angle)))
        new-y (+ (:y p2) (* direction distance (q/sin angle)))]
    {:x new-x :y new-y}))

; Generate an n length list of floats in the range 0.0 - 1.0
; that is "aestethically pleasing" for lerp.
; E.g: n=2 => [0.2, 0.75]
(defn lerpies [n]
  (let [step (/ 1.0 (inc n))
        perturbation 0.2]
    (->> (range 1 (inc n))
         (map #(+ (* % step) (* perturbation (- (rand) 0.5))))
         (map #(max 0.0 (min 1.0 %)))
         (sort))))

(defn gauss-size [pos max]
  (let [perturbation 0.5
        normalized-pos (/ pos (dec max))
        scale-factor (q/exp (* -0.5 (q/pow (/ (- normalized-pos 0.5) 0.5) 2)))]
    (+ (* scale-factor (q/random-gaussian))
       (* perturbation (q/random-gaussian)))))

; Idea for this function:
; Take a list of points ({:x x, :y y}):
; - keep the first and last points
; - select N number of points in between the first and last
; - For each selected point calculate the line that is perpendicular to it (compared to the line from previous selected point to this)
;   - replace the point with a new one on the perpendicular line
(defn ekg-replace [ps]
  (let [p0 (first ps)
        p-last (last ps)
        len (count ps)
        jag-count (q/random 1 8)
        indices (map #(int (q/lerp 0 len %)) (lerpies jag-count))
        pps (map-indexed
             #(let [from (nth ps (max 0 (dec %2)))
                    to (nth ps (min %2 (dec len)))
                    gs (q/abs (gauss-size %1 jag-count))
                    min-len (* (q/random 7 30) gs)
                    max-len (* (q/random (+ min-len 20) (+ min-len 55)) gs)]
                (println "gs: " gs)
                (println "min-len:" min-len ", max-len:" max-len)
                (perpendicular-point from to (q/random min-len max-len) (if (even? %1) 1 -1)))
             indices)]
    (concat [p0] pps [p-last])))

;
(defn map-some [fpredicate ftrue ffalse items]
  (map (fn [element]
         (if (fpredicate element)
           (ftrue element)
           (ffalse element)))
       items))


(defn apply-some-ekg [predicate p-segments]
  (map-some predicate ekg-replace identity p-segments))

; modified version of
; https://clojuredocs.org/clojure.core/split-with#example-5e48288ce4b0ca44402ef839
; that splits every time the predicate is true.
(defn split-by [pred coll]
  (lazy-seq
   (when-let [s (seq coll)]
     (let [[xs ys] (split-with pred s)]
       (if (seq xs)
         (cons xs (split-by pred ys))
         (let [pred (complement pred)
               skip (take-while pred s)
               others (drop-while pred s)
               [xs ys] (split-with pred others)]
           (cons (concat skip xs)
                 (split-by pred ys))))))))

(defn ekg-section [ystart yend points]
  (let [len (inc (- yend ystart))]
    (->> points
         (split-by #(or (< (:y %) ystart) (> (:y %) yend)))
         (apply-some-ekg #(= (count %) len))
         flatten)))

(defn rand-ekg-section [points]
  (let [start (int (q/random 0 (- 1754 100)))
        end (int (+ start (q/random 30 100)))]
    (println "start:" start ", end:" end)
    (ekg-section start end points)))

(defn recur-ekg-sections [i max points]
  (if (= i max)
    points
    (recur-ekg-sections (inc i) max (rand-ekg-section points))))

(defn generate-funky-wave [startx endy amp yoffset freq xoffset]
  (let [max-ekgs (int (q/random 1 8))]
    (->> (coord-gen startx endy)
         ; sinusoid-point 100 0 50.0 620 p
         (map #(sinusoid-point amp yoffset freq xoffset %))
           ;(map-some #(and (> (:y %) 400) (< (:y %) 450) (even? (:y %)))
           ;          perlin
           ;          identity)
           ;(map perlin)
         (recur-ekg-sections 0 max-ekgs))))

(defn draw-funky-wave [wave]
  (let [segments (partition 2 1 wave)]
    (doseq [[p1 p2] segments]
      (let [w (q/abs (sinusoid (:y p1) 4 0 150.0 0))
            nf (q/noise (* 0.05 (:y p1)))
            w (* 5 nf w)]
        (q/stroke-weight w)
        (q/line (:x p1) (:y p1) (:x p2) (:y p2))))))

(defn gen-many-waves []
  [(generate-funky-wave 0 1754 100 0 50.0 300)
   (generate-funky-wave 0 1754 90 0 90.0 600)
   (generate-funky-wave 0 1754 150 0 80.0 900)])

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  (q/noise-detail 8 0.5)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  {:color 0,
   :angle 0,
   :waves (gen-many-waves)})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  state)

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  (q/stroke 0 0 0)

  (doseq [wave (:waves state)]
      ;(println (str "x: " (:x p1) ", y: " (:y p1) " -> x: " (:x p2) ", y: " (:y p2))
    (draw-funky-wave wave)))

(defn filename []
  (let [timestamp (str (q/year) (q/month) (q/day) (q/hour) (q/minute) (q/seconds))]
    (str "ekg_" timestamp)))

(defn save-screenshot []
  (let [name (filename)]
    (println "Saving screenshot: " name)
    (q/save-frame (str name "-####.png"))))

(defn regenerate [state]
  (q/noise-seed (q/frame-count))
  (assoc state :waves (gen-many-waves)))

(defn mouse-clicked [state event]
  (cond
    (= (:button event) :left) (do (save-screenshot) state)
    (= (:button event) :right) (regenerate state)
    :else state))

(q/defsketch ekg
  :title "ekg"
  :size [1240 1754]
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :mouse-clicked mouse-clicked
  :features [:keep-on-top]
  :middleware [m/fun-mode])
