import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  template: `
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-info">
          <p class="brand"><strong>ShopVerse</strong> &mdash; Next-Gen Multi-Vendor Market Place</p>
          <p class="tech">Spring Boot 3.x &bull; Angular 14 &bull; Oracle Database 21c XE</p>
        </div>
        <div class="footer-copyright">
          &copy; 2026 ShopVerse. All rights reserved.
        </div>
      </div>
    </footer>
  `,
  styles: [`
    .footer {
      background-color: #ffffff;
      border-top: 1px solid var(--border-color);
      padding: 24px 16px;
      margin-top: auto;
      font-size: 0.85rem;
      color: var(--text-muted);
    }
    .footer-container {
      max-width: 1240px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 12px;
    }
    .brand {
      color: var(--text-main);
      font-weight: 500;
      margin-bottom: 2px;
    }
  `]
})
export class FooterComponent {}
