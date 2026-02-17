import React, { useState, useEffect } from 'react'
import fraudService from '../../services/fraudService'
 
export default function FraudMonitor() {
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)
  const [statusFilter, setStatusFilter] = useState('')
  const [riskLevelFilter, setRiskLevelFilter] = useState('')
 
  useEffect(() => {
    loadTransactions()
  }, [statusFilter, riskLevelFilter])
 
  const loadTransactions = async () => {
    try {
      setLoading(true)
      const params = {}
      if (statusFilter) params.status = statusFilter
      if (riskLevelFilter) params.riskLevel = riskLevelFilter
     
      const data = await fraudService.getTransactions(params)
      setTransactions(data || [])
    } catch (err) {
      console.error('Error loading transactions:', err)
      alert('Failed to load transactions. Please refresh the page.')
    } finally {
      setLoading(false)
    }
  }
 
  const handleBlock = async (id) => {
    try {
      await fraudService.blockTransaction(id, 'Blocked by admin')
      // Update the transaction status locally
      setTransactions(prevTransactions =>
        prevTransactions.map(tx =>
          tx.id === id ? { ...tx, status: 'BLOCKED' } : tx
        )
      )
      alert('Transaction blocked successfully!')
    } catch (err) {
      console.error('Block error details:', err)
      const errorMsg = err.response?.data?.message || err.message || 'Unknown error'
      alert(`Failed to block transaction: ${errorMsg}`)
    }
  }
 
  const handleApprove = async (id) => {
    try {
      await fraudService.approveTransaction(id, 'Approved by admin')
      // Update the transaction status locally
      setTransactions(prevTransactions =>
        prevTransactions.map(tx =>
          tx.id === id ? { ...tx, status: 'CLEARED' } : tx
        )
      )
      alert('Transaction approved successfully!')
    } catch (err) {
      console.error('Approve error details:', err)
      const errorMsg = err.response?.data?.message || err.message || 'Unknown error'
      alert(`Failed to approve transaction: ${errorMsg}`)
    }
  }
 
  if (loading) return <div className="d-page"><div className="d-card"><div>Loading transactions...</div></div></div>
 
  return (
    <div className="d-page">
      <div className="d-card">
        <h2 className="d-card-title">Fraud Monitoring</h2>
       
        {/* Filters */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '14px', fontWeight: '500' }}>Status:</label>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                style={{ padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: '4px', fontSize: '14px' }}
              >
                <option value="">All Status</option>
                <option value="REVIEW">Review</option>
                <option value="BLOCKED">Blocked</option>
                <option value="APPROVED">Approved</option>
                <option value="CLEARED">Cleared</option>
              </select>
            </div>
           
            <div>
              <label style={{ display: 'block', marginBottom: '4px', fontSize: '14px', fontWeight: '500' }}>Risk Level:</label>
              <select
                value={riskLevelFilter}
                onChange={(e) => setRiskLevelFilter(e.target.value)}
                style={{ padding: '8px 12px', border: '1px solid #d1d5db', borderRadius: '4px', fontSize: '14px' }}
              >
                <option value="">All Risk Levels</option>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
            </div>
          </div>
         
          <button
            onClick={() => { setStatusFilter(''); setRiskLevelFilter(''); }}
            style={{ padding: '8px 16px', backgroundColor: '#6b7280', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '14px' }}
          >
            Clear Filters
          </button>
        </div>
 
        <div className="d-table-wrap">
          <table className="d-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Status</th>
                <th>Risk Level</th>
                <th>Type</th>
                <th>Note</th>
                <th>Reason</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map(tx => {
                const noFraud = (tx.status === 'CLEARED' || tx.status === 'APPROVED') && tx.riskLevel === 'LOW'
                const reasonText = noFraud ? 'No Fraud Detected' : (tx.description || 'No description')
                return (
                <tr key={tx.id}>
                  <td>{tx.id}</td>
                  <td>
                    <span className={`d-badge ${
                      tx.status === 'REVIEW' ? 'badge-active' :
                      tx.status === 'BLOCKED' ? 'badge-inactive' :
                      tx.status === 'APPROVED' ? 'badge-success' : ''
                    }`}>
                      {tx.status}
                    </span>
                  </td>
                  <td>
                    <span className={`d-badge ${
                      tx.riskLevel === 'CRITICAL' ? 'badge-inactive' :
                      tx.riskLevel === 'HIGH' ? 'badge-active' :
                      tx.riskLevel === 'MEDIUM' ? 'badge-warning' :
                      tx.riskLevel === 'LOW' ? 'badge-success' : ''
                    }`}>
                      {tx.riskLevel}
                    </span>
                  </td>
                  <td style={{ maxWidth: '160px', fontSize: '13px', color: '#0f172a' }}>{tx.type || '—'}</td>
                  <td style={{ maxWidth: '240px', fontSize: '13px', color: '#0f172a' }}>{tx.note || '—'}</td>
                  <td style={{ maxWidth: '300px', fontSize: '13px', color: '#475569' }}>
                    {reasonText}
                  </td>
                  <td>
                    <div style={{ display: 'flex', gap: '8px' }}>
                      <button
                        className="d-btn"
                        onClick={() => handleBlock(tx.id)}
                        disabled={tx.status === 'BLOCKED' || tx.status === 'APPROVED' || tx.status === 'CLEARED'}
                      >
                        Block
                      </button>
                      <button
                        className="d-btn"
                        onClick={() => handleApprove(tx.id)}
                        disabled={tx.status === 'APPROVED' || tx.status === 'CLEARED'}
                      >
                        Approve
                      </button>
                    </div>
                  </td>
                </tr>
                )
              })}
            </tbody>
          </table>
        </div>
        {transactions.length === 0 && (
          <div className="empty-state">
            <div>No transactions found</div>
          </div>
        )}
      </div>
    </div>
  )
}
 
 
 

 