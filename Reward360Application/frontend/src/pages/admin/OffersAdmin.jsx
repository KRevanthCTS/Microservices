import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../../api/client";
import "../../styles/offersAdmin.css";

// This admin page aims to work with two API shapes found across environments:
// 1) /admin/offers (simple admin endpoints)
// 2) /api/promotions/promotions (promotion microservice)
// We try the first and fall back to the second to make local dev easier.
const ENDPOINT_CANDIDATES = ["/admin/offers", "/api/promotions/promotions"]

export default function OffersAdmin() {
  const [searchParams] = useSearchParams();
  const [items, setItems] = useState([]);
  const today = new Date().toISOString().split("T")[0];
  const [o, setO] = useState({
    title: "",
    category: "",
    description: "",
    costPoints: "",
    imageUrl: "",
    tierLevel: "",
    startDate: "",
    endDate: "",
  });
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [activeEndpoint, setActiveEndpoint] = useState(null)

  const resolveUrl = (pathSuffix = "") => {
    if (!activeEndpoint) return null
    return `${activeEndpoint}${pathSuffix}`
  }

  const load = async () => {
    setErr("")
    // If we already detected an endpoint, use it. Otherwise probe candidates.
    if (activeEndpoint) {
      try {
        const { data } = await api.get(resolveUrl(""));
        setItems(data);
        return;
      } catch (e) {
        // if probe fails, clear active and retry below
        setActiveEndpoint(null)
      }
    }

    for (const candidate of ENDPOINT_CANDIDATES) {
      try {
        const { data } = await api.get(candidate)
        setActiveEndpoint(candidate)
        setItems(data)
        return
      } catch (e) {
        // try next candidate
      }
    }
    setErr("Failed to load offers: no working API endpoint found")
  };

  useEffect(() => {
    load();
    const category = searchParams.get("category");
    const description = searchParams.get("description");
    if (category || description) {
      setO((p) => ({
        ...p,
        category: category || p.category,
        description: description || p.description,
      }));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  const validate = () => {
    if (!o.title.trim()) return "Title is required";
    // Category may be empty to indicate the offer applies to ALL categories
    // (leave blank for All categories)
    if (!o.description.trim()) return "Description is required";
    if (Number.isNaN(Number(o.costPoints)) || o.costPoints === "" || Number(o.costPoints) < 0)
      return "Cost points must be a non-negative number";
    return "";
  };

  const submit = async (e) => {
    e.preventDefault();
    setErr("");
    setMsg("");
    const v = validate();
    if (v) {
      setErr(v);
      return;
    }
    try {
  const payload = { ...o };
  payload.costPoints = Number(o.costPoints) || 0;
  // Auto-publish: mark new offers active by default
  payload.active = true;
      // If tierLevel is empty string, treat as null (All tiers)
      if (!payload.tierLevel) payload.tierLevel = null;
      // If category is empty or whitespace, treat as null (All categories)
      if (!payload.category || !payload.category.trim()) payload.category = null;

      // Try to post to the detected endpoint; if none detected, probe in load()
      const postTo = activeEndpoint || ENDPOINT_CANDIDATES[0]
      await api.post(`${postTo}`, payload)
      setMsg("Offer created successfully")
      setO({
        title: "",
        category: "",
        description: "",
        costPoints: "",
        imageUrl: "",
        tierLevel: "",
        startDate: "",
        endDate: "",
      })
      // reload list (keep detected endpoint)
      load()
    } catch (ex) {
      console.error(ex);
      setErr(ex?.response?.data?.message || ex?.message || "Failed to create offer");
    }
  };
  return (
    <div className="grid cols-2">
      <div className="card">
        <h3>Create Offer</h3>
        <form onSubmit={submit}>
          <label>Title</label>
          <input
            className="input"
            placeholder="e.g., Festive 15% Off"
            value={o.title}
            onChange={(e) => setO((p) => ({ ...p, title: e.target.value }))}
            required
          />
          <label>Category</label>
          <input
            className="input"
            placeholder="e.g., Electronics"
            value={o.category}
            onChange={(e) => setO((p) => ({ ...p, category: e.target.value }))}
          />
          <label>Description</label>
          <textarea
            className="input"
            placeholder="Short description visible to users"
            value={o.description}
            onChange={(e) =>
              setO((p) => ({ ...p, description: e.target.value }))
            }
            required
          />
          <label>Cost Points</label>
          <input
            className="input no-spinner"
            type="number"
            placeholder="e.g., 350"
            value={o.costPoints}
            onChange={(e) =>
              setO((p) => ({
                ...p,
                costPoints: e.target.value === "" ? "" : parseInt(e.target.value, 10),
              }))
            }
          />
          <label>Tier Level</label>
          <select
            className="input"
            value={o.tierLevel}
            onChange={(e) => setO((p) => ({ ...p, tierLevel: e.target.value }))}
          >
            <option value="">All</option>
            <option>Bronze</option>
            <option>Silver</option>
            <option>Gold</option>
            <option>Platinum</option>
          </select>
          <div className="date-row">
            <div className="date-field">
              <label>Start Date</label>
              <input
                className="input"
                type="date"
                min={today}
                value={o.startDate}
                onChange={(e) => setO((p) => ({ ...p, startDate: e.target.value }))}
              />
            </div>
            <div className="date-field">
              <label>End Date</label>
              <input
                className="input"
                type="date"
                min={o.startDate || today}
                value={o.endDate}
                onChange={(e) => setO((p) => ({ ...p, endDate: e.target.value }))}
              />
            </div>
          </div>
          <label>Image URL (optional)</label>
          <input
            className="input"
            placeholder="https://..."
            value={o.imageUrl}
            onChange={(e) => setO((p) => ({ ...p, imageUrl: e.target.value }))}
          />
          {err && (
            <div className="error" style={{ marginTop: 6 }}>
              {err}
            </div>
          )}
          {msg && (
            <div className="badge" style={{ marginTop: 6 }}>
              {msg}
            </div>
          )}
          <div style={{ marginTop: 10 }}>
            <button className="button">Create Offer</button>
          </div>
        </form>
      </div>
      <div className="card">
        <h3>Offers</h3>
        {items?.length === 0 ? (
          <p>No offers yet. Create one on the left.</p>
        ) : (
          <div className="offers-grid">
            {items.map((i) => (
              <div key={i.id} className="offer-card">
                {i.imageUrl && (
                  <img
                    src={i.imageUrl}
                    alt={i.title}
                    className="offer-card-img"
                    onError={(e) => {
                      e.currentTarget.style.display = "none";
                    }}
                  />
                )}
                <div className="offer-card-body">
                  <div className="offer-card-header">
                    <h4>{i.title}</h4>
                    {i.active ? (
                      <span className="badge badge-active">Active</span>
                    ) : (
                      <span className="badge badge-inactive">Inactive</span>
                    )}
                  </div>
                  <div className="offer-field">
                    <span className="field-label">ID:</span>
                    <span className="field-value">{i.id}</span>
                  </div>
                  <div className="offer-field">
                    <span className="field-label">Category:</span>
                    <span className="field-value">{i.category}</span>
                  </div>
                  <div className="offer-field">
                    <span className="field-label">Description:</span>
                    <span className="field-value">{i.description}</span>
                  </div>
                  <div className="offer-meta">
                    <span className="badge">{i.costPoints} pts</span>
                    <span className="badge">Tier: {i.tierLevel || "All"}</span>
                  </div>
                          <div className="offer-actions">
                            <button
                              className="btn btn-primary"
                              onClick={async () => {
                                try {
                                  const ep = activeEndpoint || ENDPOINT_CANDIDATES[0]
                                  // support toggle endpoints for both API shapes
                                  if (ep.startsWith('/admin')) {
                                    await api.put(`${ep}/${i.id}/toggle`)
                                  } else {
                                    // promotions microservice: PUT to /{id} used for toggle/publish in some deployments
                                    await api.put(`${ep}/${i.id}`)
                                  }
                                  // refresh list without re-probing endpoint
                                  load()
                                } catch (e) {
                                  console.error(e);
                                  setErr(e?.response?.data?.message || "Failed to toggle")
                                }
                              }}
                            >
                              {i.active ? "Unpublish" : "Publish"}
                            </button>
                            <button
                              className="btn btn-danger"
                              onClick={async () => {
                                if (!window.confirm("Delete this offer?")) return;
                                try {
                                  await api.delete(`${activeEndpoint || ENDPOINT_CANDIDATES[0]}/${i.id}`)
                                  load()
                                } catch (e) {
                                  console.error(e);
                                  setErr(e?.response?.data?.message || "Failed to delete")
                                }
                              }}
                            >
                              Delete
                            </button>
                          </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

 