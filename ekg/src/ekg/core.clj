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

(defn perlin [])

(defn generate-funky-wave [startx endy]
  (->> (coord-gen startx endy)
       (map sinusoid-point)))

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
