import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['CUSTOMER', [Validators.required]],
      phoneNumber: [''],
      addressLine1: [''],
      city: [''],
      state: [''],
      pinCode: ['']
    });
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.errorMessage = 'Please fill all required fields correctly (Name, valid Email, min 6-character Password).';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const payload = { ...this.registerForm.value };

    this.authService.register(payload).subscribe({
      next: () => {
        this.loading = false;
        // Redirect to login page with pre-filled email so user can sign in and show Ma'am
        this.router.navigate(['/auth/login'], {
          queryParams: { registered: 'true', email: payload.email }
        });
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || 'Registration failed. Please try again.';
      }
    });
  }
}
