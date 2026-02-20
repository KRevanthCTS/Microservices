import React, { useState, useEffect } from 'react';
import { Bar, Doughnut } from 'react-chartjs-2';
import 'chart.js/auto';
import * as XLSX from "xlsx";
import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";
import '../../styles/report.css';
import analyticsService from '../../services/analyticsService';

/* ─── KPI icon map ─── */
const KPI_META = {
  Users:       { icon: '👥', color: '#4f46e5', bg: 'rgba(79,70,229,0.10)' },
  Offers:      { icon: '🎁', color: '#10b981', bg: 'rgba(16,185,129,0.10)' },
  Redemptions: { icon: '🏆', color: '#f59e0b', bg: 'rgba(245,158,11,0.10)' },
};

/* ═══════════════════════════════════════════════════════════
   Main Analytics Dashboard
   Keeps: #2 Doughnut, #3 Stacked Bar, #10 Top Offers
   KPI cards + Bar chart (core)
   All wired to REAL API data
   ═══════════════════════════════════════════════════════════ */
function AnalyticsDashboard() {
  const [kpis, setKpis] = useState([]);
  const [rawKpis, setRawKpis] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  /* Real API trend data for #3 stacked bar */
  const [trendData, setTrendData] = useState({ users: {}, offers: {}, redemptions: {} });
  /* Real API offer categories for #2 */
  const [offerCategories, setOfferCategories] = useState({ labels: [], data: [] });
  /* Real API top redeemed offers for #10 */
  const [topOffers, setTopOffers] = useState([]);

  useEffect(() => {
    setLoading(true);
    setError(null);

    Promise.all([
      analyticsService.getKPIs(),
      analyticsService.getTrend('users').catch(() => ({ labels: [], data: [] })),
      analyticsService.getTrend('offers').catch(() => ({ labels: [], data: [] })),
      analyticsService.getTrend('redemptions').catch(() => ({ labels: [], data: [] })),
      analyticsService.getOffersHistory().catch(() => []),
      analyticsService.getRedemptionsHistory().catch(() => []),
    ])
      .then(([kpiData, usersTrend, offersTrend, redemptionsTrend, offersHist, redemptionsHist]) => {
        setRawKpis(kpiData);

        /* Store trend data for #3 stacked bar */
        setTrendData({ users: usersTrend, offers: offersTrend, redemptions: redemptionsTrend });

        setKpis([
          { label: 'Users', value: kpiData.users || 0 },
          { label: 'Offers', value: kpiData.offers || 0 },
          { label: 'Redemptions', value: kpiData.redemptions || 0 },
        ]);

        /* #2 — Doughnut from real offer categories */
        const catMap = {};
        (offersHist || []).forEach(o => {
          const cat = o.category || 'Other';
          catMap[cat] = (catMap[cat] || 0) + 1;
        });
        const catLabels = Object.keys(catMap);
        const catData = Object.values(catMap);
        setOfferCategories({ labels: catLabels.length ? catLabels : ['No Data'], data: catData.length ? catData : [1] });

        /* #10 — Top redeemed offers from real redemptions */
        const offerCountMap = {};
        (redemptionsHist || []).forEach(r => {
          const title = r.offerTitle || 'Unknown';
          offerCountMap[title] = (offerCountMap[title] || 0) + 1;
        });
        const sorted = Object.entries(offerCountMap)
          .map(([title, count]) => ({ title, count }))
          .sort((a, b) => b.count - a.count)
          .slice(0, 5)
          .map((item, i) => ({ ...item, rank: i + 1 }));
        setTopOffers(sorted);

        setLoading(false);
      })
      .catch(err => {
        console.error('API error:', err);
        setError('Failed to load analytics data.');
        setLoading(false);
      });
  }, []);

  /* ── Bar chart data (existing) ── */
  const barData = {
    labels: ['Users', 'Offers', 'Redemptions'],
    datasets: [{
      label: 'Count',
      data: [rawKpis.users || 0, rawKpis.offers || 0, rawKpis.redemptions || 0],
      backgroundColor: ['rgba(79,70,229,0.75)', 'rgba(16,185,129,0.75)', 'rgba(245,158,11,0.75)'],
      borderRadius: 8, borderSkipped: false, barThickness: 48,
    }],
  };
  const barOpts = {
    responsive: true,
    plugins: { legend: { display: false }, tooltip: { backgroundColor: '#1e1e2f' } },
    scales: {
      y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.04)' }, ticks: { font: { size: 12 } } },
      x: { grid: { display: false }, ticks: { font: { weight: '600' } } },
    },
  };

  /* ── #2 Doughnut — real offer categories ── */
  const doughnutColors = ['#4f46e5','#10b981','#f59e0b','#ef4444','#8b5cf6','#ec4899','#06b6d4','#84cc16'];
  const doughnutData = {
    labels: offerCategories.labels,
    datasets: [{
      data: offerCategories.data,
      backgroundColor: doughnutColors.slice(0, offerCategories.labels.length),
      hoverOffset: 6, borderWidth: 2,
    }],
  };

  /* ── #3 Stacked Bar — from real trends API ── */
  const stackedLabels = trendData.users.labels || trendData.offers.labels || trendData.redemptions.labels || [];
  const stackedData = {
    labels: stackedLabels,
    datasets: [
      { label: 'Users', data: trendData.users.data || [], backgroundColor: 'rgba(79,70,229,0.7)' },
      { label: 'Offers', data: trendData.offers.data || [], backgroundColor: 'rgba(16,185,129,0.7)' },
      { label: 'Redemptions', data: trendData.redemptions.data || [], backgroundColor: 'rgba(245,158,11,0.7)' },
    ],
  };
  const stackedOpts = {
    responsive: true, plugins: { legend: { position: 'top' } },
    scales: { x: { stacked: true }, y: { stacked: true, beginAtZero: true } },
  };

  return (
    <div className="rpt-dashboard">
      {/* Section header */}
      <div className="rpt-section-header">
        <h2 className="rpt-section-title">
          <span className="rpt-icon">📊</span> Analytics Dashboard
        </h2>
        <p className="rpt-section-sub">Real-time overview of your rewards ecosystem</p>
      </div>

      {error && <div className="rpt-alert rpt-alert-err">{error}</div>}

      {loading ? (
        <div className="rpt-loader"><div className="rpt-spinner" /><span>Loading analytics&hellip;</span></div>
      ) : (
        <>
          {/* ── KPI cards ── */}
          <div className="rpt-kpi-row">
            {kpis.map((k, i) => {
              const meta = KPI_META[k.label] || {};
              return (
                <div className="rpt-kpi-card" key={i} style={{ '--accent': meta.color, '--accent-bg': meta.bg }}>
                  <div className="rpt-kpi-icon">{meta.icon}</div>
                  <div className="rpt-kpi-body">
                    <span className="rpt-kpi-label">{k.label}</span>
                    <span className="rpt-kpi-value">{k.value}</span>
                  </div>
                  <div className="rpt-kpi-ring" />
                </div>
              );
            })}
          </div>

          {/* ── Existing Bar chart ── */}
          <div className="rpt-chart-card">
            <h3 className="rpt-chart-title">Metrics Comparison</h3>
            <div className="rpt-chart-wrap"><Bar data={barData} options={barOpts} /></div>
          </div>

          {/* ── #2 Doughnut ── */}
          <div className="rpt-feature-block">
            <div className="rpt-feature-tag">Doughnut Chart — Offer Categories</div>
            <div className="rpt-chart-card">
              <h3 className="rpt-chart-title">Offer Category Breakdown</h3>
              <div className="rpt-chart-wrap rpt-chart-sm"><Doughnut data={doughnutData} /></div>
            </div>
          </div>

          {/* ── #3 Stacked Bar ── */}
          <div className="rpt-feature-block">
            <div className="rpt-feature-tag">Stacked Bar — Period Comparison</div>
            <div className="rpt-chart-card">
              <h3 className="rpt-chart-title">Stacked Comparison</h3>
              <div className="rpt-chart-wrap"><Bar data={stackedData} options={stackedOpts} /></div>
            </div>
          </div>

          {/* ── #10 Top Redeemed Offers ── */}
          <div className="rpt-feature-block">
            <div className="rpt-feature-tag">Top Redeemed Offers</div>
            <div className="rpt-chart-card">
              <h3 className="rpt-chart-title">Top Redeemed Offers</h3>
              {topOffers.length > 0 ? (
                <table className="rpt-table">
                  <thead><tr><th>#</th><th>Offer</th><th>Redemptions</th></tr></thead>
                  <tbody>
                    {topOffers.map(o => (
                      <tr key={o.rank}>
                        <td><span className="rpt-rank">{o.rank}</span></td>
                        <td>{o.title}</td>
                        <td><strong>{o.count}</strong></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p className="rpt-empty">No redemption data available yet.</p>
              )}
            </div>
          </div>
        </>
      )}
    </div>
  );
}

/* ============================================================
   Report Generator – center-aligned export section
   ============================================================ */
function ReportGenerator() {
  const [metric, setMetric] = useState('users');
  const [exporting, setExporting] = useState(false);
  const [message, setMessage] = useState(null);

  const exportAnalyticsData = async (format) => {
    setExporting(true);
    setMessage(null);
    let header = [];
    let rows = [];

    try {
      if (metric !== "reports") {
        await analyticsService.generateReport(metric);
      }

      let data;
      if (metric === "reports") data = await analyticsService.getReportsHistory();
      else if (metric === "users") data = await analyticsService.getUsersHistory();
      else if (metric === "offers") data = await analyticsService.getOffersHistory();
      else if (metric === "redemptions") data = await analyticsService.getRedemptionsHistory();

      if (!data || data.length === 0) {
        setMessage(`No ${metric} data available to export.`);
        setExporting(false);
        return;
      }

      if (metric === "reports") {
        header = ["ID", "Metric", "Generated At"];
        rows = data.map(r => [
          r.id || '', r.metric || '',
          r.generatedAt ? new Date(r.generatedAt).toLocaleString() : '',
        ]);
      } else if (metric === "users") {
        header = ["Name", "Email", "Phone", "Role", "Created At"];
        rows = data.map(u => [u.name || '', u.email || '', u.phone || '', u.role || '', u.createdAt || '']);
      } else if (metric === "offers") {
        header = ["Title", "Category", "Description", "Cost Points", "Active", "Created At", "Tier Level"];
        rows = data.map(o => [o.title || '', o.category || '', o.description || '', o.costPoints || 0, o.active ? 'Yes' : 'No', o.startDate || '', o.tierLevel || 'All']);
      } else if (metric === "redemptions") {
        header = ["Confirmation Code", "Transaction ID", "Date", "Cost Points", "Offer Title"];
        rows = data.map(r => [r.confirmationCode || '', r.transactionId || '', r.date || '', r.costPoints || 0, r.offerTitle || '']);
      }

      if (format === "csv") {
        const csv = [header, ...rows].map(r => r.join(",")).join("\n");
        const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `${metric}-history.csv`;
        a.click();
        URL.revokeObjectURL(url);
      } else if (format === "excel") {
        const worksheet = XLSX.utils.aoa_to_sheet([header, ...rows]);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, `${metric} History`);
        XLSX.writeFile(workbook, `${metric}-history.xlsx`);
      } else if (format === "pdf") {
        const doc = new jsPDF();
        const pageW = doc.internal.pageSize.getWidth();
        const pageH = doc.internal.pageSize.getHeight();
        const title = metric.charAt(0).toUpperCase() + metric.slice(1) + " Report";
        const sanitizedRows = rows.map(row =>
          row.map(cell => (typeof cell === "string" ? cell.replace(/₹/g, "Rs.") : cell))
        );

        /* ── Branded header bar ── */
        doc.setFillColor(79, 70, 229);
        doc.rect(0, 0, pageW, 38, 'F');
        doc.setFontSize(20);
        doc.setFont("helvetica", "bold");
        doc.setTextColor(255, 255, 255);
        doc.text("Reward360", 14, 16);
        doc.setFontSize(11);
        doc.setFont("helvetica", "normal");
        doc.text(title, 14, 26);
        doc.setFontSize(9);
        doc.text(`Generated: ${new Date().toLocaleString()}`, pageW - 14, 16, { align: "right" });
        doc.text(`Total Records: ${rows.length}`, pageW - 14, 24, { align: "right" });

        /* ── Decorative accent line ── */
        doc.setFillColor(245, 158, 11);
        doc.rect(0, 38, pageW, 2, 'F');

        /* ── Data table ── */
        doc.setTextColor(0);
        autoTable(doc, {
          head: [header],
          body: sanitizedRows,
          startY: 46,
          styles: {
            fontSize: 9,
            cellPadding: 2.5,
            lineColor: [229, 231, 235],
            lineWidth: 0.25,
          },
          headStyles: {
            fillColor: [79, 70, 229],
            textColor: 255,
            fontStyle: 'bold',
            fontSize: 9.5,
          },
          alternateRowStyles: {
            fillColor: [248, 247, 255],
          },
          bodyStyles: {
            textColor: [30, 30, 47],
          },
          margin: { left: 14, right: 14 },
        });

        /* ── Footer with page numbers ── */
        const totalPages = doc.internal.getNumberOfPages();
        for (let i = 1; i <= totalPages; i++) {
          doc.setPage(i);
          doc.setFillColor(249, 250, 251);
          doc.rect(0, pageH - 16, pageW, 16, 'F');
          doc.setDrawColor(229, 231, 235);
          doc.line(0, pageH - 16, pageW, pageH - 16);
          doc.setFontSize(8);
          doc.setFont("helvetica", "normal");
          doc.setTextColor(107, 114, 128);
          doc.text("Reward360 — Confidential", 14, pageH - 6);
          doc.text(`Page ${i} of ${totalPages}`, pageW - 14, pageH - 6, { align: "right" });
        }

        doc.save(`${metric}-report.pdf`);
      }

      setMessage(`Successfully exported ${metric} data as ${format.toUpperCase()}!`);
      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      console.error("Export error:", err);
      setMessage(`Failed to export ${metric} data. Error: ${err.message}`);
    } finally {
      setExporting(false);
    }
  };

  return (
    <div className="rpt-chart-card rpt-export-section">
      <div className="rpt-section-header">
        <h2 className="rpt-section-title">
          <span className="rpt-icon">📑</span> Report Generation
        </h2>
        <p className="rpt-section-sub">Select a dataset and export in your preferred format.</p>
      </div>

      {message && (
        <div className={`rpt-alert ${message.includes('Failed') ? 'rpt-alert-err' : 'rpt-alert-ok'}`}>
          {message}
        </div>
      )}

      <div className="rpt-export-controls">
        <select className="rpt-select" value={metric} onChange={e => setMetric(e.target.value)}>
          <option value="users">Users</option>
          <option value="offers">Offers</option>
          <option value="redemptions">Redemptions</option>
          <option value="reports">Reports</option>
        </select>
      </div>

      <div className="rpt-export-btns">
        {['pdf', 'excel', 'csv'].map(fmt => (
          <button
            key={fmt}
            className="rpt-export-btn"
            onClick={() => exportAnalyticsData(fmt)}
            disabled={exporting}
          >
            <span className="rpt-export-btn-icon">
              {fmt === 'pdf' ? '📄' : fmt === 'excel' ? '📗' : '📋'}
            </span>
            {exporting ? 'Exporting...' : `Export ${fmt.toUpperCase()}`}
          </button>
        ))}
      </div>
    </div>
  );
}

/* Main Reports Page */
export default function Reports() {
  return (
    <div className="rpt-page">
      <AnalyticsDashboard />
      <ReportGenerator />
    </div>
  );
}
 
 