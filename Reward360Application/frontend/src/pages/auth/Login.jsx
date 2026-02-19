
import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../../api/client'
import { useUser } from '../../context/UserContext'

export default function Login(){
  const [form, setForm] = useState({ email:'', password:'', role:'USER', mode:'Password' })
  const [err, setErr] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [roleMismatch, setRoleMismatch] = useState(false)
  const navigate = useNavigate()
  const { refreshAll } = useUser()

  const onChange = e=> setForm(p=>({...p, [e.target.name]: e.target.value}))
  const submit = async e=>{
    e.preventDefault()
    setErr('')
    try{
      const {data} = await api.post('/auth/login', { email: form.email, password: form.password })
      if (data.role && data.role.toUpperCase() !== (form.role || '').toUpperCase()) {
        setRoleMismatch(true)
        return
      }
      localStorage.setItem('token', data.token)
      localStorage.setItem('role', data.role)
      try {
        await refreshAll()
      } catch (refreshErr) {
        console.warn('Failed to refresh user data after login', refreshErr)
      }
      if(data.role==='ADMIN') navigate('/admin')
      else navigate('/user')
    }catch(ex){ setErr('Invalid credentials') }
  }

  return (
    <>
    <div className="auth-page auth-centered">
      <div className="auth-form-panel">
        <div className="auth-card fade-in-up">
          <h2 className="auth-heading">Login</h2>
          <p className="auth-subheading">Pick your role, then sign in with your credentials.</p>

          <form onSubmit={submit} className="auth-form">
            {/* Role toggle */}
            <div className="auth-role-toggle">
              <button
                type="button"
                className={`role-btn ${form.role === 'USER' ? 'active' : ''}`}
                onClick={() => setForm(p => ({...p, role: 'USER'}))}
              >
                👤 User
              </button>
              <button
                type="button"
                className={`role-btn ${form.role === 'ADMIN' ? 'active' : ''}`}
                onClick={() => setForm(p => ({...p, role: 'ADMIN'}))}
              >
                🛡️ Admin
              </button>
            </div>

            {/* Email */}
            <div className="auth-field">
              <label className="auth-label">Email</label>
              <div className="auth-input-wrap">
                <span className="auth-input-icon">✉️</span>
                <input
                  className="auth-input"
                  name="email"
                  placeholder="you@example.com"
                  value={form.email}
                  onChange={onChange}
                  required
                />
              </div>
            </div>

            {/* Password */}
            <div className="auth-field">
              <label className="auth-label">Password</label>
              <div className="auth-input-wrap">
                <span className="auth-input-icon">🔒</span>
                <input
                  className="auth-input"
                  name="password"
                  placeholder="Enter your password"
                  type={showPassword ? 'text' : 'password'}
                  value={form.password}
                  onChange={onChange}
                  required
                  minLength={8}
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
            </div>

            {err && <div className="auth-error">{err}</div>}

            <button className="auth-submit" type="submit">Sign In</button>
          </form>

          <div className="auth-footer-links">
            <Link className="link" to="/forgot">Forgot Password?</Link>
            <span>New here? <Link className="link" to="/register">Register</Link></span>
          </div>
        </div>
      </div>
    </div>

    {roleMismatch && (
      <div className="modal-overlay" onClick={() => setRoleMismatch(false)}>
        <div className="card modal-card" onClick={(e)=>e.stopPropagation()}>
          <div style={{fontSize:56,marginBottom:12}}>⚠️</div>
          <h3 style={{margin:'0 0 8px',color:'#dc2626',fontSize:22}}>Role Mismatch</h3>
          <p style={{color:'#475569',marginBottom:20}}>The selected role does not match your account role. Please select the correct role and try again.</p>
          <button className="button" onClick={()=>setRoleMismatch(false)} style={{minWidth:140}}>OK</button>
        </div>
      </div>
    )}
    </>
  )
}
