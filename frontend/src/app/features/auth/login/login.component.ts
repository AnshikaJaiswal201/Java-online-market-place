import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';
  registeredSuccess = false;
  returnUrl = '/';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['registered'] === 'true') {
        this.registeredSuccess = true;
        const regEmail = params['email'] || '';
        if (regEmail) {
          this.loginForm.patchValue({ email: regEmail });
        }
      } else if (this.authService.isLoggedIn()) {
        this.redirectUser();
      }
    });
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.loading = false;
        this.redirectUser();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Login failed. Please check your credentials.';
      }
    });
  }

  private redirectUser(): void {
    if (this.authService.isSeller()) {
      this.router.navigate(['/seller/dashboard']);
    } else {
      this.router.navigate([this.returnUrl !== '/' ? this.returnUrl : '/products']);
    }
  }

  // Quick fill test credentials
  fillCredentials(email: string): void {
    this.loginForm.patchValue({
      email: email,
      password: 'Password@123'
    });
  }
}
