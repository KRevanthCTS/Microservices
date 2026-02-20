import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../api/client'

export default function Register(){
  const [f, setF] = useState({ name:'', email:'', phone:'', password:'', role:'USER', preferences:[], communication:'Email' })
  const [err, setErr] = useState({})
  const [showPassword, setShowPassword] = useState(false)
  const [showSuccess, setShowSuccess] = useState(false)
  const navigate = useNavigate()

  const validate = ()=>{
    const e={}
    if(!f.name) e.name='Name is required'
    if(!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(f.email)) e.email='Valid email is required'
    if(!/^\d{10}$/.test(f.phone)) e.phone='Phone must be 10 digits'
    if(f.password.length<8) e.password='Password must be at least 8 characters'
    return e
  }

  const onChange = (e)=>{
    const {name, value, checked} = e.target
    if(name==='preferences'){
      const arr = new Set(f.preferences)
      if(checked) arr.add(value); else arr.delete(value)
      setF(p=>({...p, preferences:[...arr]}));
    } else setF(p=>({...p, [name]: value}))
  }

  const submit = async (e)=>{
    e.preventDefault()
    const v = validate(); setErr(v)
    if(Object.keys(v).length) return
    try{
      await api.post('/auth/register', {
        name:f.name, email:f.email, phone:f.phone, password:f.password,
        role:f.role, preferences:f.preferences.join(','), communication:f.communication
      })
      setShowSuccess(true)
    }catch(ex){ setErr({ api:'Registration failed' }) }
  }

  return (
    <>
    <div className="auth-page auth-centered">
      <div className="auth-form-panel">
        <div className="auth-card auth-card-wide fade-in-up">
          <h2 className="auth-heading">Create Account</h2>
          <p className="auth-subheading">Fill in your details to get started with Reward360.</p>

          <form onSubmit={submit} className="auth-form">
            {/* Role toggle */}
            <div className="auth-role-toggle">
              <button
                type="button"
                className={`role-btn ${f.role === 'USER' ? 'active' : ''}`}
                onClick={() => setF(p => ({...p, role: 'USER'}))}
              >
                👤 User
              </button>
              <button
                type="button"
                className={`role-btn ${f.role === 'ADMIN' ? 'active' : ''}`}
                onClick={() => setF(p => ({...p, role: 'ADMIN'}))}
              >
                🛡️ Admin
              </button>
            </div>

            {/* Two-column field grid */}
            <div className="auth-grid-2">
              {/* Name */}
              <div className="auth-field">
                <label className="auth-label">Name</label>
                <div className="auth-input-wrap">
                  <span className="auth-input-icon">👤</span>
                  <input className="auth-input" name="name" value={f.name} onChange={onChange} placeholder="Enter your name" />
                </div>
                {err.name && <div className="auth-error">{err.name}</div>}
              </div>

              {/* Email */}
              <div className="auth-field">
                <label className="auth-label">Email</label>
                <div className="auth-input-wrap">
                  <span className="auth-input-icon">✉️</span>
                  <input className="auth-input" name="email" value={f.email} onChange={onChange} placeholder="you@example.com" />
                </div>
                {err.email && <div className="auth-error">{err.email}</div>}
              </div>

              {/* Phone */}
              <div className="auth-field">
                <label className="auth-label">Phone Number</label>
                <div className="auth-input-wrap">
                  <span className="auth-input-icon">📱</span>
                  <input className="auth-input" name="phone" value={f.phone} onChange={onChange} placeholder="10-digit number" />
                </div>
                {err.phone && <div className="auth-error">{err.phone}</div>}
              </div>

              {/* Password */}
              <div className="auth-field">
                <label className="auth-label">Password</label>
                <div className="auth-input-wrap">
                  <span className="auth-input-icon">🔒</span>
                  <input
                    className={`auth-input${showPassword ? '' : ' auth-input-masked'}`}
                    type="text"
                    autoComplete="off"
                    name="password"
                    value={f.password}
                    onChange={onChange}
                    placeholder="Min 8 characters"
                  />
                  <button
                    type="button"
                    className="auth-eye-btn"
                    onClick={() => setShowPassword(s => !s)}
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                  >
                    {showPassword ? (
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M1.5 12s4-7.5 10.5-7.5S22.5 12 22.5 12s-4 7.5-10.5 7.5S1.5 12 1.5 12z" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><circle cx="12" cy="12" r="3" stroke="#0b5ed7" strokeWidth="1.5"/></svg>
                    ) : (
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M17.94 17.94A10.94 10.94 0 0 1 12 19.5C5.5 19.5 1.5 12 1.5 12c1.46-2.57 3.76-4.7 6.54-6.02" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M3 3l18 18" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/><path d="M9.88 9.88A3 3 0 0 0 14.12 14.12" stroke="#0b5ed7" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/></svg>
                    )}
                  </button>
                </div>
                {err.password && <div className="auth-error">{err.password}</div>}
              </div>
            </div>

            {/* Preferences */}
            <div className="auth-field">
              <label className="auth-label">Preferences (for Users)</label>
              <div className="auth-checkbox-group">
                {['Fashion','Electronics','Groceries','Beauty','Home'].map(opt => (
                  <label key={opt} className="auth-checkbox-label">
                    <input type="checkbox" name="preferences" value={opt} onChange={onChange} />
                    <span>{opt}</span>
                  </label>
                ))}
              </div>
            </div>

            {/* Communication */}
            <div className="auth-field">
              <label className="auth-label">Communication Preference</label>
              <div className="auth-radio-group">
                {['Email','SMS','WhatsApp'].map(c => (
                  <label key={c} className="auth-radio-label">
                    <input type="radio" name="communication" value={c} checked={f.communication===c} onChange={onChange} />
                    <span>{c}</span>
                  </label>
                ))}
              </div>
            </div>

            {err.api && <div className="auth-error">{err.api}</div>}

            <button className="auth-submit" type="submit">Create Account</button>
          </form>

          <div className="auth-footer-links" style={{justifyContent:'center'}}>
            <span>Already have an account? <Link className="link" to="/login">Login</Link></span>
          </div>
        </div>
      </div>
    </div>

    {showSuccess && (
      <div className="modal-overlay">
        <div className="card modal-card" onClick={(e)=>e.stopPropagation()}>
          <div style={{fontSize:56,marginBottom:12}}>🎉</div>
          <h3 style={{margin:'0 0 8px',color:'#059669',fontSize:22}}>Registration Successful!</h3>
          <p style={{color:'#475569',marginBottom:20}}>Your account has been created successfully. You can now log in with your credentials.</p>
          <button className="button" onClick={()=>navigate('/login')} style={{minWidth:140}}>Go to Login</button>
        </div>
      </div>
    )}
    </>
  )
}
