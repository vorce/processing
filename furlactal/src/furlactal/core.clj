(ns furlactal.core
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def phi 1.6180339887498948482)

(defn current [previous goal step-size]
  (let [posx (first previous)
        posy (second previous)]
    [(+ posx (* (- (first goal) posx) step-size))
     (+ posy (* (- (second goal) posy) step-size))]))

(defn color-pairs [] [[(q/color 255 250 125 10), (q/color 150 230 250 10)]
                      [(q/color 155 225 190 15), (q/color 200 230 245 10)]])

(defn distance-based-color [point focal color-pair]
  (let [dnorm (-> (q/dist (first point) (second point) (first focal) (second focal))
                  (/ 250)
                  (min 1.0))]
    (q/lerp-color (first color-pair) (second color-pair) dnorm)))

(defn current-color [point focal]
  (let [dnorm (-> (q/dist (first point) (second point) (first focal) (second focal))
                  (/ 250)
                  (min 1.0))
        focal-color (q/color 255 250 125 10)
        fade-color (q/color 150 230 250 10)]
    (q/lerp-color focal-color fade-color dnorm)))

(defn draw-current [point goal color]
  (let [c-color (distance-based-color point goal color)]
    (q/stroke c-color)
    (q/with-translation [(first point)
                         (second point)]
      (q/point 0 0))))

(defn pentagon [width height]
  [[(/ width 2), 10]
   [10, (/ height 3)]
   [(- width 10), (/ height 3)]
   [(/ width 4), (- height 10)]
   [(- width (/ width 4)), (- height 10)]])

(defn random-anchors [width height amount]
  (vec (map #(do %& [(rand-int width) (rand-int height)]) (range amount))))

(defn hexagon [width height]
  [[(/ width 2), 20]
   [20, (/ height 3)]
   [(- width 20), (/ height 3)]
   [20, (- height (/ height 3))]
   [(- width 20), (- height (/ height 3))]
   [(/ width 2), (- height 20)]])

(defn anchor-points [width height style]
  (cond (= style :hexagon) (hexagon width height)
        (= style :pentagon) (pentagon width height)
        (= style :random) (random-anchors width height (max 3 (rand-int 10)))))

(defn draw-anchor-points [anchors]
  (q/stroke 255 255 255)
  (doseq [point anchors]
    (q/with-translation [(first point)
                         (second point)]
      (q/point 0 0))))

(defn clear-background []
  (q/background 0))

(defn generate-new-anchors [style]
  (let [aps (anchor-points (q/width) (q/height) style)]
    (println "Generating new anchor points:" aps)
    aps))

(defn step-size []
  (rand-nth [0.5 (/ 1 (Math/PI)) (/ 1 phi) (/ 2 3)]))

(defn setup []
  (q/frame-rate 60)
  (clear-background)
  (q/blend-mode :add)
  (let [ap (generate-new-anchors :pentagon)
        step (step-size)
        colors (rand-nth (color-pairs))
        state {:anchor-points ap
               :step step
               :colors colors}]
    (println "Initial state:" state)
    state))

(def current-position (atom [0 0]))
(def current-goal (atom [0 0]))

(defn goal [candidates previous]
  (loop [selected (rand-nth candidates)]
    (if-not (= selected previous)
      selected
      (recur (rand-nth candidates)))))

(defn new-goal [current anchors]
  (goal anchors current))

(defn update-state [state]
  state)

(defn draw-state [state]
  (doseq [i (range 300)]
    (swap! current-position current @current-goal (:step state))
    (draw-current @current-position @current-goal (:colors state))
    (swap! current-goal new-goal (:anchor-points state))))

(defn base64-encode [inp]
  (.encodeToString (java.util.Base64/getEncoder) (.getBytes inp)))

(defn filename [state]
  (let [timestamp (str (q/year) (q/month) (q/day) (q/hour) (q/minute) (q/seconds))
        serialized-state (base64-encode (pr-str state))]
    (str timestamp "_" serialized-state)))

(defn save-screenshot [state]
  (let [name (filename state)
        full (str name ".png")]
    (println "Saving screenshot: " full)
    (q/save full)))

(defn mouse-clicked [state event]
  (cond
    (= (:button event) :left) (do (save-screenshot state) state)
    (= (:button event) :right) (do (clear-background)
                                   (let [new-state (assoc state
                                                          :anchor-points (generate-new-anchors :random)
                                                          :step (step-size)
                                                          :colors (rand-nth (color-pairs)))]
                                     (println "State:" state "=>" new-state)
                                     new-state))
    :else state))

(q/defsketch furlactal
  :title "Furlactal"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :mouse-clicked mouse-clicked
  :features [:keep-on-top]
  :middleware [m/fun-mode])
