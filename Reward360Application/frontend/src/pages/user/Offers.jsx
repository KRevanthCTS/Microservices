import React, { useState } from "react";
import { useUser } from "../../context/UserContext";
import "../../styles/Offer.css";

export default function Offers() {
  const { user, offers, redeemOffer, loading, redemptions } = useUser();
  const [confirm, setConfirm] = useState(null);
  const [insufficientModal, setInsufficientModal] = useState(null);
  const [alreadyRedeemedModal, setAlreadyRedeemedModal] = useState(false);
  const [successModal, setSuccessModal] = useState(false);
  const [errorModal, setErrorModal] = useState(false);
  const [category, setCategory] = useState("All");

  if (loading || !user) return <div className="offers-page">Loading...</div>;

  const redeemedTitles = new Set((redemptions || []).map((r) => r.offerTitle));
  const pointsBalance = (user.profile && user.profile.pointsBalance) || user.pointsBalance || 0;

  const open = (o) => {
    if (pointsBalance < o.costPoints) {
      setInsufficientModal(o);
      return;
    }
    setConfirm(o);
  };
  const close = () => { setConfirm(null); setInsufficientModal(null); setAlreadyRedeemedModal(false); setSuccessModal(false); setErrorModal(false); };

  const redeem = async () => {
    if (!confirm) return;
    try {
      await redeemOffer(confirm.id, "Online");
      setConfirm(null);
      setSuccessModal(true);
    } catch (error) {
      setConfirm(null);
      setErrorModal(true);
    }
  };

  return (
    <div className="offers-page">
      <div className="o-hero">
        <div className="o-hero-overlay">
          <div className="o-hero-row">
            <div>
              <h3 className="o-hero-title">Member Offers</h3>
              <div className="o-hero-sub">
                  <span className="o-user-chip">
                    <span className="o-user-dot" />
                    {user.name || user.customerName}
                  </span>
                  <span className="o-sep">•</span>
                  <span className="o-tier">
                    Tier:&nbsp;
                    <strong>{(user.profile && user.profile.loyaltyTier) || user.loyaltyTier || "Bronze"}</strong>
                  </span>
              </div>
            </div>

              <div className="o-hero-balance-badge" title="Current Points Balance">
              <span className="o-balance-label">Balance</span>
              <span className="o-balance-value">{pointsBalance}</span>
              <span className="o-balance-unit">pts</span>
            </div>
          </div>
        </div>
      </div>

      <div style={{ display: "flex", justifyContent: "flex-end", alignItems: "center", gap: 12, margin: "18px 0", width: "100%" }}>
        <label style={{ fontWeight: 600 }}>Category:</label>
        <select value={category} onChange={(e) => setCategory(e.target.value)} style={{ padding: "6px 10px", borderRadius: 6, border: "1px solid #ccc" }}>
          <option value="All">All</option>
          {Array.from(new Set(offers.map((o) => o.category).filter(Boolean))).map((c) => (
            <option key={c} value={c}>{c}</option>
          ))}
        </select>
      </div>

      <div className="o-grid">
        {offers
          .filter((o) => category === "All" || !o.category || o.category === category)
          .map((o) => (
            <div className="o-card o-offer" key={o.id}>
              <div className="o-img-wrap">
                <img className="o-img" src={o.imageUrl} alt={o.title} />
              </div>
              <div className="o-body">
                <h4 className="o-card-title">{o.title}</h4>
                <p className="o-desc">{o.description}</p>
                <div className="o-row">
                  <span className="o-pill">
                    Cost: <strong>{o.costPoints}</strong> pts
                  </span>
                  {redeemedTitles.has(o.title) ? (
                    <button
                      className="o-btn"
                      style={{ opacity: 0.5, cursor: "not-allowed" }}
                      onClick={() => setAlreadyRedeemedModal(true)}
                    >
                      Redeemed
                    </button>
                  ) : pointsBalance < o.costPoints ? (
                    <button
                      className="o-btn"
                      style={{ opacity: 0.5, cursor: "not-allowed" }}
                      onClick={() => open(o)}
                    >
                      Redeem
                    </button>
                  ) : (
                    <button
                      className="o-btn"
                      onClick={() => open(o)}
                    >
                      Redeem
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
      </div>

      {confirm && (
        <div className="o-modal-backdrop" onClick={close} aria-hidden="true">
          <div className="o-modal-card o-card" onClick={(e) => e.stopPropagation()}>
            <div className="o-modal-grid">
              <div className="o-modal-img-wrap">
                <img className="o-modal-img" src={confirm.imageUrl} alt={confirm.title} />
              </div>
              <div className="o-modal-body">
                <h4 className="o-card-title">{confirm.title}</h4>
                <p className="o-desc">{confirm.description}</p>
                <div className="o-modal-meta">
                  <div className="o-meta-row">
                    <span className="o-meta-label">Cost</span>
                    <span className="o-meta-value">{confirm.costPoints} pts</span>
                  </div>
                </div>
                <div className="o-modal-actions">
                  <button className="o-btn" onClick={redeem}>Confirm &amp; Redeem</button>
                  <button className="o-btn o-btn-ghost" onClick={close}>Cancel</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {insufficientModal && (
        <div className="o-modal-backdrop" onClick={close} aria-hidden="true">
          <div className="o-modal-card o-card" onClick={(e) => e.stopPropagation()}>
            <div className="o-modal-body" style={{ textAlign: "center", padding: "32px 24px" }}>
              <div style={{ fontSize: 48, marginBottom: 12 }}>⚠️</div>
              <h4 className="o-card-title" style={{ color: "#dc2626", marginBottom: 8 }}>Insufficient Balance</h4>
              <p className="o-desc" style={{ marginBottom: 16 }}>
                You don't have enough points to redeem <strong>{insufficientModal.title}</strong>.
              </p>
              <div className="o-modal-meta" style={{ marginBottom: 16 }}>
                <div className="o-meta-row">
                  <span className="o-meta-label">Offer Cost</span>
                  <span className="o-meta-value">{insufficientModal.costPoints} pts</span>
                </div>
                <div className="o-meta-row">
                  <span className="o-meta-label">Your Balance</span>
                  <span className="o-meta-value" style={{ color: "#dc2626" }}>{pointsBalance} pts</span>
                </div>
                <div className="o-meta-row">
                  <span className="o-meta-label">Need More</span>
                  <span className="o-meta-value" style={{ color: "#f59e0b" }}>{insufficientModal.costPoints - pointsBalance} pts</span>
                </div>
              </div>
              <button className="o-btn o-btn-ghost" onClick={close} style={{ minWidth: 120 }}>OK</button>
            </div>
          </div>
        </div>
      )}

      {alreadyRedeemedModal && (
        <div className="o-modal-backdrop" onClick={close} aria-hidden="true">
          <div className="o-modal-card o-card" onClick={(e) => e.stopPropagation()}>
            <div className="o-modal-body" style={{ textAlign: "center", padding: "32px 24px" }}>
              <div style={{ fontSize: 48, marginBottom: 12 }}>😅</div>
              <h4 className="o-card-title" style={{ color: "#f59e0b", marginBottom: 8 }}>Oops!</h4>
              <p className="o-desc" style={{ marginBottom: 20, fontSize: 16 }}>
                Already redeemed buddy 🎉
              </p>
              <button className="o-btn o-btn-ghost" onClick={close} style={{ minWidth: 120 }}>Got it</button>
            </div>
          </div>
        </div>
      )}

      {successModal && (
        <div className="o-modal-backdrop" onClick={close} aria-hidden="true">
          <div className="o-modal-card o-card" onClick={(e) => e.stopPropagation()}>
            <div className="o-modal-body" style={{ textAlign: "center", padding: "32px 24px" }}>
              <div style={{ fontSize: 48, marginBottom: 12 }}>🎉</div>
              <h4 className="o-card-title" style={{ color: "#059669", marginBottom: 8 }}>Successfully Redeemed!</h4>
              <p className="o-desc" style={{ marginBottom: 20, fontSize: 16 }}>
                Your offer has been redeemed. Check the Redemptions page for details!
              </p>
              <button className="o-btn o-btn-ghost" onClick={close} style={{ minWidth: 120 }}>Awesome!</button>
            </div>
          </div>
        </div>
      )}

      {errorModal && (
        <div className="o-modal-backdrop" onClick={close} aria-hidden="true">
          <div className="o-modal-card o-card" onClick={(e) => e.stopPropagation()}>
            <div className="o-modal-body" style={{ textAlign: "center", padding: "32px 24px" }}>
              <div style={{ fontSize: 48, marginBottom: 12 }}>❌</div>
              <h4 className="o-card-title" style={{ color: "#dc2626", marginBottom: 8 }}>Redemption Failed</h4>
              <p className="o-desc" style={{ marginBottom: 20, fontSize: 16 }}>
                Something went wrong. Please try again later.
              </p>
              <button className="o-btn o-btn-ghost" onClick={close} style={{ minWidth: 120 }}>OK</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}