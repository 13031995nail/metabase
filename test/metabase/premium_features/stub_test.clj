(ns metabase.premium-features.stub-test
  (:require
   [clojure.test :refer :all]
   [metabase.config.core :as config]
   [metabase.premium-features.core :as premium-features]
   [metabase.premium-features.settings :as premium-features.settings]
   [metabase.premium-features.stub :as stub]
   [metabase.premium-features.token-check :as token-check]
   [metabase.util.malli.registry :as mr]))

(deftest profiles-test
  (let [self-hosted (set (stub/features-for-profile :self-hosted))
        cloud       (set (stub/features-for-profile :cloud))]
    (is (contains? self-hosted :advanced-permissions))
    (is (contains? self-hosted :custom-viz))
    (is (not (contains? self-hosted :hosting)))
    (is (contains? cloud :hosting))
    (is (every? cloud self-hosted))
    (is (thrown? clojure.lang.ExceptionInfo
                 (stub/features-for-profile :unknown)))))

(deftest test-token-status-schema-test
  (with-redefs [config/stub-enabled? true
                config/stub-profile  :self-hosted]
    (let [status (stub/stub-token-status)]
      (is (mr/validate token-check/TokenStatus status))
      (is (true? (:valid status)))
      (is (:canonical? status))
      (is (every? string? (:features status)))
      (is (contains? (set (:features status)) "advanced-permissions"))
      (is (not (contains? (set (:features status)) "hosting")))))
  (with-redefs [config/stub-enabled? false]
    (is (thrown? clojure.lang.ExceptionInfo (stub/stub-token-status)))))

(deftest cloud-token-status-test
  (with-redefs [config/stub-enabled? true
                config/stub-profile  :cloud]
    (let [status (stub/stub-token-status)]
      (is (mr/validate token-check/TokenStatus status))
      (is (contains? (set (:features status)) "hosting")))))

(deftest explicit-stub-checker-test
  (with-redefs [config/stub-enabled? true
                config/stub-profile  :self-hosted]
    (let [status (token-check/check-token token-check/stub-token-checker nil)]
      (is (true? (:valid status)))
      (is (mr/validate token-check/TokenStatus status)))))

(deftest effective-status-consistency-test
  (with-redefs [config/stub-enabled? true
                config/stub-profile  :self-hosted
                premium-features.settings/premium-embedding-token (constantly nil)]
    (let [status (token-check/-token-status)
          actual (token-check/*token-features*)]
      (is (true? (:valid status)))
      (is (= (set (:features status)) actual))
      (is (= "Enterprise" (premium-features/plan-alias)))
      (is (= [] (premium-features/quotas)))
      (is (= {} (premium-features/meters)))
      (is (false? (premium-features/is-trial?)))
      (is (true? (premium-features/has-feature? :custom-viz)))
      (is (false? (premium-features/has-feature? :nonexistent-feature)))
      (is (true? (premium-features/canonically-has-feature? :sandboxes)))
      (is (false? (premium-features/canonically-has-feature? :nonexistent-feature))))))
