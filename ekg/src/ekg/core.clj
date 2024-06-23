(ns ekg.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

; https://www.desmos.com/calculator/w9jrdpvsmk?lang=sv-SE
(defn sinusoid [y amp yoffset freq xoffset]
  (+ (* amp (q/sin (/ (- y yoffset) freq))) xoffset))

(defn sinusoid-point [p]
  (assoc p :x (sinusoid (:y p) 100 0 50.0 620)))

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
    (if (= xdivisor 0)
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
        i1 (int (q/lerp 0 len 0.1))
        i2 (int (q/lerp 0 len 0.4))
        i3 (int (q/lerp 0 len 0.65))
        i4 (int (q/lerp 0 len 0.8))]
    ;(println ps)
    [p0
     (perpendicular-point (nth ps (min 0 (dec i1))) (nth ps i1) 10 1)
     (perpendicular-point (nth ps (min 0 (dec i2))) (nth ps i2) 75 -1)
     (perpendicular-point (nth ps (min 0 (dec i3))) (nth ps i3) 80 1)
     (perpendicular-point (nth ps (min 0 (dec i4))) (nth ps i4) 15 -1)
     p-last]))

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

(defn generate-funky-wave [startx endy]
  (->> (coord-gen startx endy)
       (map sinusoid-point)
       ;(map-some #(and (> (:y %) 400) (< (:y %) 450) (even? (:y %)))
       ;          perlin
       ;          identity)
       ;(map perlin)
       (split-by #(or (< (:y %) 400) (> (:y %) 430)))
       (apply-some-ekg #(< (count %) 100))
       flatten))

(defn draw-funky-wave [wave]
  (let [segments (partition 2 1 wave)]
    (doseq [[p1 p2] segments]
       ;(println (str "x: " (:x p1) ", y: " (:y p1) " -> x: " (:x p2) ", y: " (:y p2))
      (q/line (:x p1) (:y p1) (:x p2) (:y p2)))))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  ;(let [coords (map (fn [n] {:x (sinusoid n 100 0 50.0 620), :y n}) (range 1754))]
    ;(println (str coords))
  {:color 0,
   :angle 0})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  state)

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  (q/stroke 0 0 0)

  (let [wave1 (generate-funky-wave 0 1754)]
      ;(println (str "x: " (:x p1) ", y: " (:y p1) " -> x: " (:x p2) ", y: " (:y p2))
    (draw-funky-wave wave1)))

(defn filename []
  (let [timestamp (str (q/year) (q/month) (q/day) (q/hour) (q/minute) (q/seconds))]
    (str "ekg_" timestamp)))

(defn save-screenshot []
  (let [name (filename)]
    (println "Saving screenshot: " name)
    (q/save-frame (str name "-####.png"))))

(defn mouse-clicked [state event]
  (cond
    (= (:button event) :left) (do (save-screenshot) state)
    :else state))

(q/defsketch ekg
  :title "ekg"
  :size [1240 1754]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :mouse-clicked mouse-clicked
  :features [:keep-on-top]
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
