(ns metabase.premium-features.stub
  "Test stub for enterprise license checking.
   When MB_LICENSE_STUB_ENABLED=true, returns a fixed TokenStatus with all enterprise features enabled."
  (:require
   [metabase.config.core :as config]))

(set! *warn-on-reflection* true)

(def self-hosted-features
  "Enterprise features available in self-hosted profile.
   These are the feature keywords that Metabase expects."
  [:advanced-permissions
   :audit-app
   :sandboxes
   :custom-viz
   :schema-viewer
   :remote-sync
   :serialization
   :transforms-basic
   :transforms-python
   :embedding
   :embedding-sdk
   :embedding-simple
   :whitelabel
   :sso-jwt
   :sso-saml
   :sso-oidc
   :sso-ldap
   :sso-google
   :scim
   :multi-factor-auth
   :session-timeout-config
   :disable-password-login
   :dashboard-subscription-filters
   :content-translation
   :content-verification
   :official-collections
   :snippet-collections
   :serialization
   :email-restrict-recipients
   :upload-management
   :collection-cleanup
   :database-auth-providers
   :database-routing
   :data-apps-preview
   :dependencies
   :support-users
   :library
   :table-data-editing
   :tenants
   :metabot-v3
   :metabase-ai-managed
   :admin-security-center
   :ai-controls
   :data-complexity-score
   :writable-connection
   :email-allow-list
   :cache-granular-controls
   :cache-preemptive
   :config-text-file
   :query-reference-validation
   :embedded-dashboard-count
   :embedded-question-count
   :external-users
   :internal-users
   :domains
   :tenant-users
   :tenants-with-active-users-count])

(def cloud-features
  "Enterprise features available in cloud profile (includes hosted-specific features)."
  (vec (concat self-hosted-features
               [:hosting
                :attached-dwh
                :cloud-custom-smtp
                :etl-connections
                :etl-connections-pg
                :semantic-search
                :library-retrieval])))

(defn features-for-profile
  "Return the list of feature keywords for the given profile.
   Returns a vector of keywords for use in the token status."
  [profile]
  (condp = profile
    :self-hosted (vec self-hosted-features)
    :cloud cloud-features
    (throw (ex-info (format "Unknown profile: %s" profile) {:profile profile}))))

(defn stub-token-status
  "Return a fixed TokenStatus fixture for testing.
   Always returns valid=true with a far-future expiration.

   Returns a TokenStatus map that conforms to the TokenStatus schema.
   The :features field is a vector of strings, not keywords."
  []
  (when-not config/stub-enabled?
    (throw (ex-info "stub-token-status called when stub is disabled"
                    {:cause :stub/not-enabled})))
  ;; Features must be strings per TokenStatus schema, not keywords
  {:valid         true
   :status        "valid"
   :canonical?    true
   :features      (mapv name (features-for-profile config/stub-profile))
   :plan-alias    "Enterprise"
   :trial         false
   :valid-thru    "2099-12-31"
   :max-users     9999
   :company       "Local Enterprise E2E Test"
   :quotas        []
   :meters        {}})
