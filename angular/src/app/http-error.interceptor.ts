import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ToastService } from './toast.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let message: string;

      if (error.error instanceof ErrorEvent) {
        message = error.error.message || 'Network error';
      } else {
        message = error.error?.message || getDefaultErrorMessage(error.status);
      }

      toastService.showError(message);
      return throwError(() => error);
    })
  );
};

function getDefaultErrorMessage(status: number): string {
  switch (status) {
    case 0:
      return 'Network error - please check your connection';
    case 400:
      return 'Invalid request';
    case 401:
      return 'Unauthorized - please log in';
    case 403:
      return 'Access denied';
    case 404:
      return 'Resource not found';
    case 409:
      return 'Conflict - data already exists';
    case 500:
      return 'Server error - please try again later';
    default:
      return 'An error occurred';
  }
}
