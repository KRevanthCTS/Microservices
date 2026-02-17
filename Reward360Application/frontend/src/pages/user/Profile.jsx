import React, { useState } from 'react'
import { useUser } from '../../context/UserContext'
import userService from '../../services/userService'
import '../../styles/Profile.css'

export default function Profile() {
  const { user, loading, fetchUser } = useUser()
  const [edit, setEdit] = useState(false)
  const [form, setForm] = useState({ customerName:'', preferences:'', communication:'' })

  if (loading || !user) return <div className="profile-page">Loading...</div>

  const startEdit = () => {
    setForm({
      customerName: user.customerName || '',
      preferences: user.preferences || '',
      communication: user.communication || ''
    })
    setEdit(true)
  }

  const save = async (e) => {
    e.preventDefault()
    try {
      await userService.updateProfile(form)
      setEdit(false)
      await fetchUser()
    } catch (err) { console.error(err) }
  }

  return (
    <div className="profile-page">
      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">{user.customerName?.charAt(0)}</div>
          <h2 className="profile-name">{user.customerName}</h2>
          <span className="profile-tier">{user.loyaltyTier} Member</span>
        </div>
        <div className="profile-details">
          {!edit ? (
            <>
              <div className="profile-item"><span>Points Balance</span><strong>{user.pointsBalance}</strong></div>
              <div className="profile-item"><span>Preferences</span><strong>{user.preferences || '—'}</strong></div>
              <div className="profile-item"><span>Communication</span><strong>{user.communication || 'Email'}</strong></div>
              <button className="button" onClick={startEdit}>Edit Profile</button>
            </>
          ) : (
            <form onSubmit={save}>
              <input className="input" name="customerName" value={form.customerName} onChange={e => setForm({...form, customerName: e.target.value})} />
              <input className="input" name="preferences" value={form.preferences} onChange={e => setForm({...form, preferences: e.target.value})} />
              <button className="button" type="submit">Save Changes</button>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}