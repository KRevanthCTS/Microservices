
import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import logo from '../assets/Logo.svg'

export default function Header(){
  const navigate = useNavigate()
  const role = localStorage.getItem('role')
  const [menuOpen, setMenuOpen] = useState(false)
  const logout = ()=>{ localStorage.clear(); navigate('/login') }
  const closeMenu = () => setMenuOpen(false)
  return (
    <header>
      <div className="navbar flex"> 
        <Link to="/" className="brand" onClick={closeMenu}>
          <img src={logo} alt="Rewards360" className="brand-logo" />
          <strong>Rewards360</strong>
        </Link>
        <button className="hamburger" onClick={() => setMenuOpen(p => !p)} aria-label="Toggle menu">
          <span className={`hamburger-line ${menuOpen ? 'open' : ''}`} />
          <span className={`hamburger-line ${menuOpen ? 'open' : ''}`} />
          <span className={`hamburger-line ${menuOpen ? 'open' : ''}`} />
        </button>
        <nav className={menuOpen ? 'nav-open' : ''}>
          {role==='ADMIN' && (<>
            <Link className="nav-link" to="/admin" onClick={closeMenu}>Promotions</Link>
            <Link className="nav-link" to="/admin/offers" onClick={closeMenu}>Offers</Link>
            <Link className="nav-link" to="/admin/fraud" onClick={closeMenu}>Fraud Monitor</Link>
            <Link className="nav-link" to="/admin/reports" onClick={closeMenu}>Report</Link>
          </>)}
          {role==='USER' && (<>
            <Link className="nav-link" to="/user" onClick={closeMenu}>Dashboard</Link>
            <Link className="nav-link" to="/user/profile" onClick={closeMenu}>Profile</Link>
            <Link className="nav-link" to="/user/offers" onClick={closeMenu}>Offers</Link>
            <Link className="nav-link" to="/user/redemptions" onClick={closeMenu}>Redemptions</Link>
            <Link className="nav-link" to="/user/transactions" onClick={closeMenu}>Transactions</Link>
          </>)}
          {!role && ( <>
              <Link className="nav-link auth-link" to="/login" onClick={closeMenu}>Login</Link>
              <Link className="button" to="/register" onClick={closeMenu} style={{marginLeft:8}}>Register</Link>
            </>
          )}
          {role && <button className="button logout" onClick={()=>{closeMenu();logout()}}>Logout</button>}
        </nav>
      </div>
    </header>
  )
}
