export interface User {
    id: string;
    name: string;
    email: string;
    role: 'admin' | 'customer';
}

export interface Product {
    id: string;
    name: string;
    price: number;
    description?: string;
}

export interface Order {
    id: string;
    userId: string;
    productIds: string[];
    totalAmount: number;
    status: 'pending' | 'completed' | 'canceled';
}

export interface Response<T> {
    success: boolean;
    data?: T;
    error?: string;
}