(ns roughsleeper.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn rough-point [x y roughness]
  (let [len (/ roughness 2)
        x-span [(inc (- x len)), (dec (+ x len))]
        y-span [(inc (- y len)), (dec (+ y len))]
        px (q/random (first x-span) (second x-span))
        py (q/random (first y-span) (second y-span))]
    ; (cond
    ;   (> px (+ x len)) (println (str "px" px ">" (+ x len)))
    ;   (> py (+ y len)) (println (str "py" py ">" (+ y len)))
    ;   (< px (- x len)) (println (str "px" px "<" (- x len)))
    ;   (< py (- y len)) (println (str "py" py "<" (- y len)))
    ;   :else (println (str "(x:" x ", y:" y ")" ", (px:" px ", py:" py "), " x-span ", " y-span)))

    ; (q/no-fill)
    ; (q/stroke 255 50 50 128)
    ; (q/point x y)
    ; (q/ellipse x y roughness roughness)
    ; (q/with-translation [px py]
    ;   (q/stroke 50 50 200)
    ;   (q/point 0 0))
    [px py]))

(defn single-rough-line [[startx starty] [endx endy] roughness]
  (let [len (q/dist startx starty endx endy)
        roughness-for-len (q/lerp (/ roughness 4) roughness (/ len (q/width)))
        [startx-r starty-r] (rough-point startx starty roughness-for-len)
        [midx-r midy-r] (rough-point (/ (+ startx endx) 2) (/ (+ starty endy) 2) roughness-for-len)
        [mid2x-r mid2y-r] (rough-point (+ (/ startx 3) (* (/ 2 3) endx)) (+ (/ starty 3) (* (/ 2 3) endy)) roughness-for-len)
        [endx-r endy-r] (rough-point endx endy roughness-for-len)]
    ; (q/fill 50 255 50 128)
    ; (q/ellipse startx starty 10 10)
    ; (q/ellipse endx endy 10 10)
    ; (q/fill 255 50 50 128)
    ; (q/ellipse startx-r starty-r 10 10)
    ; (q/ellipse midx-r midy-r 10 10)
    ; (q/ellipse mid2x-r mid2y-r 10 10)
    ; (q/ellipse endx-r endy-r 10 10)
    ; (q/no-fill)
    ;(println (str "Roughness:" roughness ", scaled" roughness-for-len))
    (q/bezier startx-r starty-r midx-r midy-r mid2x-r mid2y-r endx-r endy-r)))

(defn rough-line [[startx starty] [endx endy] roughness]
  (single-rough-line [startx starty] [endx endy] roughness)
  (single-rough-line [endx endy] [startx starty] roughness))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 5)
  ; Set color mode to HSB (HSV) instead of default RGB.
  ;(q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)})

(defn hand-drawn-box [x y roughness]
  (let [len 220
        tilt 30
        depth (/ len 4)]
    (rough-line [x y] [(+ x len) (+ y 15)] roughness) ; top left -> top mid
    (rough-line [x y] [(- x tilt) (+ y depth)] roughness) ; top left -> mid left
    (rough-line [(- x tilt) (+ y depth)] [(- (+ x len) tilt 15) (+ y depth tilt)] roughness) ; mid left -> mid mid
    (rough-line [(- (+ x len) tilt 15) (+ y depth tilt)] [(+ x len) (+ y 15)] roughness) ; mid right -> top mid

    (rough-line [(- x tilt) (+ y depth)] [(+ (- x tilt) 10) (+ y depth tilt)] roughness) ; mid left -> bottom left
    (rough-line [(+ (- x tilt) 10) (+ y depth tilt)] [(- (+ x len) tilt 15) (+ y depth tilt tilt)] roughness) ; bottom left -> bottom mid
    (rough-line [(- (+ x len) tilt 10) (+ y depth tilt tilt)] [(- (+ x len) tilt 15) (+ y depth tilt)] roughness) ; bottom mid -> top mid
    (rough-line [(- (+ x len) tilt 10) (+ y depth tilt tilt)] [(+ (+ x len) 5) (+ y tilt 15)] roughness) ; /
    (rough-line [(+ (+ x len) 5) (+ y tilt 15)] [(+ x len) (+ y 15)] roughness)))


(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 240)
  (q/no-fill)

  (q/stroke 50 50 50 250)
  (q/stroke-weight 3)
  (hand-drawn-box 130 40 50)

  (q/stroke-weight 2)
  (hand-drawn-box 130 200 20)

  (q/stroke-weight 1)
  (hand-drawn-box 130 360 10))

(defn filename []
  (let [timestamp (str (q/year) (q/month) (q/day) (q/hour) (q/minute) (q/seconds))
        ;serialized-state (base64-encode (pr-str state))
        ]
    (str "skiss_" timestamp)))

(defn save-screenshot []
  (let [name (filename)
        full (str name ".png")]
    (println "Saving screenshot: " full)
    (q/save full)))

(defn mouse-clicked [state event]
  (cond
    (= (:button event) :left) (do (save-screenshot) state)
    :else state))

(q/defsketch roughsleeper
  :title "Skiss"
  :size [500 500]
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
