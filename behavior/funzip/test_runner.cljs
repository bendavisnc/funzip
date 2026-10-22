(ns funzip.test-runner
  (:require [speclj.core]))

(defn main [& _args]
  (js/speclj.run.standard.arm)
  (js/speclj.run.standard.run_specs "color" true))