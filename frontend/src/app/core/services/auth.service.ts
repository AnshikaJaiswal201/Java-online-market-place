import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, AuthResponse, User } from '../../shared/models/models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadStoredUser();
  }

  private loadStoredUser(): void {
    const storedUser = localStorage.getItem('user_details');
    if (storedUser) {
      try {
        this.currentUserSubject.next(JSON.parse(storedUser));
      } catch (e) {
        this.logout();
      }
    }
  }

  login(credentials: { email: string; password: string }): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/login`, credentials).pipe(
      tap(res => {
        if (res.success && res.data) {
          this.setSession(res.data);
        }
      })
    );
  }

  register(userData: any): Observable<ApiResponse<AuthResponse>> {
    this.logout();
    return this.http.post<ApiResponse<AuthResponse>>(`${this.apiUrl}/register`, userData);
  }

  private setSession(authData: AuthResponse): void {
    localStorage.setItem('auth_token', authData.token);
    const user: User = {
      userId: authData.userId,
      email: authData.email,
      fullName: authData.fullName,
      roles: authData.roles
    };
    localStorage.setItem('user_details', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  logout(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_details');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  hasRole(role: string): boolean {
    const user = this.currentUserValue();
    if (!user || !user.roles) return false;
    const normalized = role.startsWith('ROLE_') ? role : `ROLE_${role}`;
    return user.roles.includes(normalized);
  }

  isCustomer(): boolean {
    return this.hasRole('ROLE_CUSTOMER');
  }

  isSeller(): boolean {
    return this.hasRole('ROLE_SELLER');
  }
}
